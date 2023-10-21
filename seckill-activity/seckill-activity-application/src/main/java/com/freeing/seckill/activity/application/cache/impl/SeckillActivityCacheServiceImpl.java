package com.freeing.seckill.activity.application.cache.impl;

import cn.hutool.core.date.SystemClock;
import com.alibaba.fastjson.JSON;
import com.freeing.seckill.activity.application.builder.SeckillActivityBuilder;
import com.freeing.seckill.activity.application.cache.SeckillActivityCacheService;
import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;
import com.freeing.seckill.activity.domain.service.SeckillActivityDomainService;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.cache.model.SeckillBusinessCache;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.lock.DistributedLock;
import com.freeing.seckill.common.lock.factory.DistributedLockFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private SeckillActivityDomainService seckillActivityDomainService;

    @Override
    public SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activitYId, boolean doubleCheck) {
        logger.info("tryUpdateSeckillActivityCacheByLock|更新秒杀活动缓存|{}", activitYId);
        DistributedLock lock = distributedLockFactory
            .getDistributedLock(SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY)));

        try {
            boolean isLockSuccess = lock.tryLock(1, 1, TimeUnit.SECONDS);
            if (!isLockSuccess) {
                return new SeckillBusinessCache<SeckillActivity>().retryLater();
            }
            SeckillBusinessCache<SeckillActivity> seckillActivityCache;
            if (doubleCheck) {
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
