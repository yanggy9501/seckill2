package com.freeing.seckill.goods.application.cache.impl;

import com.freeing.seckill.goods.application.builder.SeckillGoodsBuilder;
import com.freeing.seckill.goods.application.cache.SeckillGoodsCacheService;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.cache.local.LocalCacheService;
import com.freeing.seckill.common.cache.model.SeckillBusinessCache;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.lock.DistributedLock;
import com.freeing.seckill.common.lock.factory.DistributedLockFactory;
import com.freeing.seckill.goods.domain.model.entity.SeckillGoods;
import com.freeing.seckill.goods.domain.service.SeckillGoodsDomainService;
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
 * 获取商品信息
 *
 * @author yanggy
 */
@Service
public class SeckillGoodsCacheServiceImpl implements SeckillGoodsCacheService {
    private final static Logger logger = LoggerFactory.getLogger(SeckillGoodsCacheServiceImpl.class);

    /**
     * 更新秒杀商品的分布式缓存时的锁前缀
     */
    private static final String SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY = "SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY_";

    /**
     * 本地缓存更新|锁
     */
    private final Lock localCacheUpdatelock = new ReentrantLock();

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private LocalCacheService<Long, SeckillBusinessCache<SeckillGoods>> localCacheService;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private SeckillGoodsDomainService seckillGoodsDomainService;

    @Override
    public String buildCacheKey(Object key) {
        return String.join(":", SeckillConstants.SECKILL_GOODS_CACHE_KEY, String.valueOf(key));
    }

    @Override
    public SeckillBusinessCache<SeckillGoods> getSeckillGoods(Long goodsId, Long version) {
        // 从本地缓存获取数据
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = localCacheService.getIfPresent(goodsId);
        if (Objects.nonNull(seckillGoodsCache)) {
            // 版本号为空，则直接返回本地缓存中的数据
            if (Objects.isNull(version)) {
                logger.info("SeckillGoodsCache|命中本地缓存|{}", goodsId);
                return seckillGoodsCache;
            }
            // 传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(seckillGoodsCache.getVersion()) <= 0) {
                logger.info("SeckillGoodsCache|命中本地缓存|{}", goodsId);
                return seckillGoodsCache;
            }
        }
        // 从分布式缓存中获取缓存并更新本地缓存
        return getDistributedCache(goodsId);
    }

    @Override
    public SeckillBusinessCache<SeckillGoods> tryUpdateSeckillGoodsCacheByLock(Long goodsId, boolean doubleCheck) {
       logger.info("SeckillGoodsCache|更新分布式缓存|{}", goodsId);
        String lockKey = SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(goodsId));
        DistributedLock lock = distributedLockFactory.getDistributedLock(lockKey);
        try {
            try {
                boolean locked = lock.tryLock(2, 5, TimeUnit.SECONDS);
                if (BooleanUtils.isFalse(locked)) {
                    return new SeckillBusinessCache<SeckillGoods>().retryLater();
                }

                SeckillBusinessCache<SeckillGoods> seckillGoodsCache;
                // 双重检查
                if (doubleCheck) {
                    seckillGoodsCache = SeckillGoodsBuilder.getSeckillBusinessCache(
                        distributedCacheService.getObject(buildCacheKey(goodsId)), SeckillGoods.class);
                    if (Objects.nonNull(seckillGoodsCache)) {
                        return seckillGoodsCache;
                    }
                }
                // 更新分布式缓存
                SeckillGoods seckillGoods = seckillGoodsDomainService.getSeckillGoodsId(goodsId);
                seckillGoodsCache = Objects.nonNull(seckillGoodsDomainService) ?
                    new SeckillBusinessCache<SeckillGoods>().with(seckillGoods).withVersion(System.currentTimeMillis()) :
                    new SeckillBusinessCache<SeckillGoods>().notExist();

                distributedCacheService.put(buildCacheKey(goodsId), seckillGoodsCache);
                logger.info("SeckillGoodsCache|分布式缓存已经更新|{}", goodsId);
                return seckillGoodsCache;
            } catch (Exception e) {
                logger.error("SeckillGoodsCache|更新分布式缓存失败|{}", goodsId);
                return new SeckillBusinessCache<SeckillGoods>().retryLater();
            }
        } finally {
            lock.unlock();
        }
    }

    private SeckillBusinessCache<SeckillGoods> getDistributedCache(Long goodsId) {
        logger.info("SeckillGoodsCache|读取分布式缓存|{}", goodsId);
        Object cacheObj = distributedCacheService.getObject(buildCacheKey(goodsId));
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache =
            SeckillGoodsBuilder.getSeckillBusinessCache(cacheObj, SeckillGoods.class);
        // 分布式缓存中没有数据，则尝试更新分布式缓存中的数据，注意的是只用一个线程去更新分布式缓存中的数据
        if (Objects.isNull(seckillGoodsCache)) {
            seckillGoodsCache = tryUpdateSeckillGoodsCacheByLock(goodsId, true);
        }

        // 获取的数据不为空，并且不需要重试；更新本地缓存
        if (Objects.nonNull(seckillGoodsCache) && !seckillGoodsCache.isRetryLater()) {
            if (localCacheUpdatelock.tryLock()) {
                try {
                    localCacheService.put(goodsId, seckillGoodsCache);
                    logger.info("SeckillGoodsCache|本地缓存已经更新|{}", goodsId);
                } finally {
                    localCacheUpdatelock.unlock();
                }
            }
        }
        return seckillGoodsCache;
    }
}
