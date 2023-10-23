package com.freeing.seckill.activity.application.cache.impl;

import cn.hutool.core.date.SystemClock;
import com.alibaba.fastjson.JSON;
import com.freeing.seckill.activity.application.builder.SeckillActivityBuilder;
import com.freeing.seckill.activity.application.cache.SeckillActivityCacheService;
import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;
import com.freeing.seckill.activity.domain.service.SeckillActivityDomainService;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.cache.local.LocalCacheService;
import com.freeing.seckill.common.cache.model.SeckillBusinessCache;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.lock.DistributedLock;
import com.freeing.seckill.common.lock.factory.DistributedLockFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yanggy
 */
@Service
public class SeckillActivityCacheServiceImpl implements SeckillActivityCacheService {
    private final static Logger logger = LoggerFactory.getLogger(SeckillActivityCacheServiceImpl.class);

    /**
     * 更新活动时获取分布式锁使用
     */
    private static final String SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY = "SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY_";

    private final Lock localCacheUpdatelock = new ReentrantLock();

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private LocalCacheService<Long, SeckillBusinessCache<SeckillActivity>> localCacheService;

    @Autowired
    private SeckillActivityDomainService seckillActivityDomainService;

    @Override
    public SeckillBusinessCache<SeckillActivity> getCachedSeckillActivity(Long activityId, Long version) {
        // 先从本地缓存中获取数据
        SeckillBusinessCache<SeckillActivity> seckillActivityCache = localCacheService.getIfPresent(activityId);
        if (Objects.nonNull(seckillActivityCache)) {
            // 传递的版本号为空，则直接返回本地缓存中的数据
            if (version == null) {
                logger.info("SeckillActivityCache|名字本地缓存|{}", JSON.toJSONString(seckillActivityCache));;
                return seckillActivityCache;
            }
            // 传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(seckillActivityCache.getVersion()) <= 0){
                logger.info("SeckillActivityCache|命中本地缓存|{}", activityId);
                return seckillActivityCache;
            }
            // 传递的版本号大于缓存中的版本号，说明缓存中的数据比较落后，从分布式缓存获取数据并更新到本地缓存
            if (version.compareTo(seckillActivityCache.getVersion()) > 0) {
                return getDistributedCache(activityId);
            }

        }
        // 从分布式缓存中获取数据，并设置到本地缓存中
        return getDistributedCache(activityId);
    }

    private SeckillBusinessCache<SeckillActivity> getDistributedCache(Long activityId) {
        logger.info("SeckillActivityCache|读取分布式缓存|{}", activityId);
        Object seckillActivityCacheObj = distributedCacheService.getObject(buildCacheKey(activityId));
        SeckillBusinessCache<SeckillActivity> seckillActivityCache =
            SeckillActivityBuilder.getSeckillBusinessCache(seckillActivityCacheObj, SeckillActivity.class);
        // 分布式缓存中没有数据
        if (Objects.isNull(seckillActivityCache)) {
            logger.info("SeckillActivityCache|分布式缓存未命中|{}", activityId);
            // 尝试更新分布式缓存中的数据，注意的是只用一个线程去更新分布式缓存中的数据
            seckillActivityCache = tryUpdateSeckillActivityCacheByLock(activityId, true);
        }
        // 获取的数据不为空，并且不需要重试
        if (Objects.nonNull(seckillActivityCache) && BooleanUtils.isFalse(seckillActivityCache.isRetryLater())) {
            // 获取本地锁，更新本地缓存
            if (localCacheUpdatelock.tryLock()) {
                try {
                    localCacheService.put(activityId, seckillActivityCache);
                } finally {
                    localCacheUpdatelock.unlock();
                }
            }
        }
        return seckillActivityCache;
    }


    @Override
    public SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activitYId, boolean doubleCheck) {
        logger.info("tryUpdateSeckillActivityCacheByLock|更新秒杀活动缓存|{}", activitYId);
        DistributedLock lock = distributedLockFactory
            .getDistributedLock(SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY)));

        try {
            boolean isLockSuccess = lock.tryLock(1, 1, TimeUnit.SECONDS);
            // false 则其他线程已经获取锁进行更新操作，当前线程快速返回，且告诉前台稍后重试
            if (!isLockSuccess) {
                return new SeckillBusinessCache<SeckillActivity>().retryLater();
            }
            SeckillBusinessCache<SeckillActivity> seckillActivityCache;
            if (doubleCheck) {
                // 获取锁成功后，再次从缓存中获取数据，防止高并发下多个线程争抢锁的过程中，后续的线程再等待1秒的过程中，前面的线程释放了锁，后续的线程获取锁成功后再次更新分布式缓存数据
                Object cacheValue = distributedCacheService.getObject(buildCacheKey(activitYId));
                seckillActivityCache = SeckillActivityBuilder.getSeckillBusinessCache(cacheValue, SeckillActivity.class);
                if (seckillActivityCache != null){
                    return seckillActivityCache;
                }
            }

            SeckillActivity seckillActivity = seckillActivityDomainService.getSeckillActivityById(activitYId);
            if (Objects.isNull(seckillActivity)) {
                seckillActivityCache = new SeckillBusinessCache<SeckillActivity>().notExist();
            } else {
                seckillActivityCache = new SeckillBusinessCache<SeckillActivity>()
                    .with(seckillActivity)
                    .withVersion(SystemClock.now());
            }

            // 将数据保存到分布式缓存
            distributedCacheService
                .put(buildCacheKey(activitYId), JSON.toJSONString(seckillActivityCache), SeckillConstants.FIVE_MINUTES);
            logger.info("tryUpdateSeckillActivityCacheByLock|分布式缓存已经更新|{}", activitYId);
            return seckillActivityCache;
        } catch (InterruptedException e) {
            logger.warn("tryUpdateSeckillActivityCacheByLock|更新分布式缓存失败|{}", activitYId);
            return new SeckillBusinessCache<SeckillActivity>().retryLater();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return String.join(":", SeckillConstants.SECKILL_ACTIVITY_CACHE_KEY, String.valueOf(key));
    }
}
