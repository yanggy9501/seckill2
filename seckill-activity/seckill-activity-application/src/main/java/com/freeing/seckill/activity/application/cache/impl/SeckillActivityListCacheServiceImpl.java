package com.freeing.seckill.activity.application.cache.impl;

import cn.hutool.core.date.SystemClock;
import com.alibaba.fastjson.JSON;
import com.freeing.seckill.activity.application.builder.SeckillActivityBuilder;
import com.freeing.seckill.activity.application.cache.SeckillActivityListCacheService;
import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;
import com.freeing.seckill.activity.domain.service.SeckillActivityDomainService;
import com.freeing.seckill.common.builder.SeckillCommonBuilder;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.cache.local.LocalCacheService;
import com.freeing.seckill.common.cache.model.SeckillBusinessCache;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.lock.DistributedLock;
import com.freeing.seckill.common.lock.factory.DistributedLockFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yanggy
 */
@Service
public class SeckillActivityListCacheServiceImpl implements SeckillActivityListCacheService {
    private final static Logger logger = LoggerFactory.getLogger(SeckillActivityListCacheServiceImpl.class);

    private static final String SECKILL_ACTIVITES_UPDATE_CACHE_LOCK_KEY = "SECKILL_ACTIVITIES_UPDATE_CACHE_LOCK_KEY_";

    private final Lock localCacheUpdatelock = new ReentrantLock();

    @Autowired
    private LocalCacheService<Long, SeckillBusinessCache<List<SeckillActivity>>> localCacheService;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private SeckillActivityDomainService seckillActivityDomainService;


    @Override
    public SeckillBusinessCache<List<SeckillActivity>> getCachedActivities(Integer status, Long version) {
        // 获取本地缓存
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache =
            localCacheService.getIfPresent(status.longValue());
        if (Objects.nonNull(seckillActivitiyListCache)) {
            if (Objects.isNull(version)) {
                logger.info("SeckillActivitesCache|命中本地缓存|{}", status);
                return seckillActivitiyListCache;
            }
            // 传递过来的版本小于或等于缓存中的版本号
            if (version.compareTo(seckillActivitiyListCache.getVersion()) <= 0) {
                logger.info("SeckillActivitesCache|命中本地缓存|{}", status);
                return seckillActivitiyListCache;
            }
            if (version.compareTo(seckillActivitiyListCache.getVersion()) > 0) {
                return getDistributedCache(status);
            }
        }
        return getDistributedCache(status);
    }

    private SeckillBusinessCache<List<SeckillActivity>> getDistributedCache(Integer status) {
        logger.info("SeckillActivitesCache|读取分布式缓存|{}", status);
        Object cacheObject = distributedCacheService.getObject(buildCacheKey(status));
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache =
            SeckillActivityBuilder.getSeckillBusinessCacheList(cacheObject, SeckillActivity.class);
        if (seckillActivitiyListCache == null) {
            seckillActivitiyListCache = tryUpdateSeckillActivityCacheByLock(status, true);
        }
        if (seckillActivitiyListCache != null &&  !seckillActivitiyListCache.isRetryLater()) {
            if (localCacheUpdatelock.tryLock()) {
                try {
                    localCacheService.put(status.longValue(), seckillActivitiyListCache);
                    logger.info("SeckillActivitesCache|本地缓存已经更新|{}", status);
                }finally {
                    localCacheUpdatelock.unlock();
                }
            }
        }
        return seckillActivitiyListCache;
    }

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> getCachedActivities(Date currentTime, Integer status, Long version) {
        return null;
    }

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivityCacheByLock(Integer status, boolean doubleCheck) {
        logger.info("SeckillActivitesCache|更新分布式缓存|{}", status);
        DistributedLock lock = distributedLockFactory
            .getDistributedLock(SECKILL_ACTIVITES_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(status)));
        try {
            boolean isLockSuccess = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (BooleanUtils.isFalse(isLockSuccess)) {
                return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
            }

            SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache;

            if (doubleCheck) {
                Object cacheObject = distributedCacheService.getObject(buildCacheKey(status));
                seckillActivitiyListCache = SeckillCommonBuilder.getSeckillBusinessCacheList(cacheObject, SeckillActivity.class);
                if (Objects.nonNull(seckillActivitiyListCache)) {
                    return seckillActivitiyListCache;
                }
            }
            List<SeckillActivity> seckillActivityList = seckillActivityDomainService.getSeckillActivityList(status);
            if (CollectionUtils.isEmpty(seckillActivityList)) {
                seckillActivitiyListCache = new SeckillBusinessCache<List<SeckillActivity>>().notExist();
            } else {
                seckillActivitiyListCache = new SeckillBusinessCache<List<SeckillActivity>>()
                    .with(seckillActivityList)
                    .withVersion(SystemClock.now());
            }
            distributedCacheService.put(buildCacheKey(status), JSON.toJSONString(seckillActivitiyListCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillActivitesCache|分布式缓存已经更新|{}", status);
            return seckillActivitiyListCache;
        } catch (InterruptedException  e) {
            logger.info("SeckillActivitesCache|更新分布式缓存失败|{}", status);
            return  new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivityCacheByLock(Date currentTime, Integer status, boolean doubleCheck) {
        long key = currentTime.getTime() + status.longValue();
        logger.info("SeckillActivitesCache|更新分布式缓存|{}", key);
        DistributedLock lock = distributedLockFactory
            .getDistributedLock(SECKILL_ACTIVITES_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(status)));
        try {
            boolean isLockSuccess = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (BooleanUtils.isFalse(isLockSuccess)){
                return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
            }
            SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache;
            if (doubleCheck){
                //获取锁成功后，再次从缓存中获取数据，防止高并发下多个线程争抢锁的过程中，后续的线程再等待1秒的过程中，前面的线程释放了锁，后续的线程获取锁成功后再次更新分布式缓存数据
                seckillActivitiyListCache = SeckillActivityBuilder
                    .getSeckillBusinessCacheList(distributedCacheService.getObject(buildCacheKey(key)), SeckillActivity.class);
                if (Objects.nonNull(seckillActivitiyListCache)){
                    return seckillActivitiyListCache;
                }
            }
            List<SeckillActivity> seckillActivityList =
                seckillActivityDomainService.getSeckillActivityListBetweenStartTimeAndEndTime(currentTime, status);
            if (CollectionUtils.isEmpty(seckillActivityList)) {
                seckillActivitiyListCache = new SeckillBusinessCache<List<SeckillActivity>>().notExist();
            } else {
                seckillActivitiyListCache = new SeckillBusinessCache<List<SeckillActivity>>()
                    .with(seckillActivityList)
                    .withVersion(SystemClock.now());
            }
            distributedCacheService.put(buildCacheKey(key), JSON.toJSONString(seckillActivitiyListCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillActivitesCache|分布式缓存已经更新|{}", key);
            return seckillActivitiyListCache;
        } catch (InterruptedException e) {
            logger.info("SeckillActivitesCache|更新分布式缓存失败|{}", key);
            return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return String.join(":", SeckillConstants.SECKILL_ACTIVITIES_CACHE_KEY, String.valueOf(key));
    }
}
