package com.freeing.seckill.goods.application.cache.impl;

import com.alibaba.fastjson.JSON;
import com.freeing.seckill.goods.application.builder.SeckillGoodsBuilder;
import com.freeing.seckill.goods.application.cache.SeckillGoodsListCacheService;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.cache.local.LocalCacheService;
import com.freeing.seckill.common.cache.model.SeckillBusinessCache;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.lock.DistributedLock;
import com.freeing.seckill.common.lock.factory.DistributedLockFactory;
import com.freeing.seckill.goods.domain.model.entity.SeckillGoods;
import com.freeing.seckill.goods.domain.service.SeckillGoodsDomainService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 秒杀商品缓存数据
 *
 * @author yanggy
 */
@Service
public class SeckillGoodsListCacheServiceImpl implements SeckillGoodsListCacheService {
    private final static Logger logger = LoggerFactory.getLogger(SeckillGoodsListCacheServiceImpl.class);

    private static final String SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY = "SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY_";

    private final Lock localCacheUpdatelock = new ReentrantLock();

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private LocalCacheService<Long, SeckillBusinessCache<List<SeckillGoods>>> localCacheService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private SeckillGoodsDomainService seckillGoodsDomainService;

    @Override
    public String buildCacheKey(Object key) {
        return String.join(":", SeckillConstants.SECKILL_GOODSES_CACHE_KEY, String.valueOf(key));
    }

    @Override
    public SeckillBusinessCache<List<SeckillGoods>> getCachedGoodsList(Long activityId, Long version) {
        // 获取本地缓存中的数据
        SeckillBusinessCache<List<SeckillGoods>> cachedGoodsList = localCacheService.getIfPresent(activityId);
        if (Objects.nonNull(cachedGoodsList)) {
            // 版本号为空，表示命中本地缓存
            if (Objects.isNull(version)) {
                logger.info("SeckillGoodsListCache|命中本地缓存|{}", activityId);
                return cachedGoodsList;
            }
            // 传递的版本号比缓存中的版本号小，则直接返回缓存中的数据
            if (version.compareTo(cachedGoodsList.getVersion()) <= 0) {
                logger.info("SeckillGoodsListCache|命中本地缓存|{}", activityId);
                return cachedGoodsList;
            }
        }
        return getDistributedCache(activityId);
    }

    @Override
    public SeckillBusinessCache<List<SeckillGoods>> tryUpdateSeckillGoodsCacheByLock(Long activityId, boolean doubleCheck) {
        logger.info("SeckillGoodsListCache|更新分布式缓存|{}", activityId);
        String lockKey = SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(activityId));
        DistributedLock lock = distributedLockFactory.getDistributedLock(lockKey);

        try {
            boolean locked = lock.tryLock(2, 5, TimeUnit.SECONDS);
            if (BooleanUtils.isFalse(locked)) {
                return new SeckillBusinessCache<List<SeckillGoods>>().retryLater();
            }
            SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache;
            // 双重检查
            if (doubleCheck) {
                //获取锁成功后，再次从缓存中获取数据，防止高并发下多个线程争抢锁的过程中，后续的线程再等待1秒的过程中，前面的线程释放了锁，后续的线程获取锁成功后再次更新分布式缓存数据
                seckillGoodsListCache = SeckillGoodsBuilder.getSeckillBusinessCacheList(
                    distributedCacheService.getObject(buildCacheKey(activityId)), SeckillGoods.class);
                if (seckillGoodsListCache != null){
                    return seckillGoodsListCache;
                }
            }
            // 查询数据库
            List<SeckillGoods> seckillGoodsList = seckillGoodsDomainService.getSeckillGoodsByActivityId(activityId);
            seckillGoodsListCache = CollectionUtils.isEmpty(seckillGoodsList) ?
                new SeckillBusinessCache<List<SeckillGoods>>().notExist() :
                new SeckillBusinessCache<List<SeckillGoods>>().with(seckillGoodsList)
                    .withVersion(System.currentTimeMillis());
            // 更新分布式缓存
            distributedCacheService.put(buildCacheKey(activityId), JSON.toJSONString(seckillGoodsListCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillGoodsListCache|分布式缓存已经更新|{}", activityId);
            return seckillGoodsListCache;
        } catch (Exception e) {
            logger.info("SeckillGoodsListCache|更新分布式缓存失败|{}", activityId, e);
            return new SeckillBusinessCache<List<SeckillGoods>>().retryLater();
        } finally {
            lock.unlock();
        }
    }

    private SeckillBusinessCache<List<SeckillGoods>> getDistributedCache(Long activityId) {
        logger.info("SeckillGoodsListCache|读取分布式缓存|{}", activityId);
        Object cacheObj = distributedCacheService.getObject(buildCacheKey(activityId));
        SeckillBusinessCache<List<SeckillGoods>> seckillBusinessCacheList =
            SeckillGoodsBuilder.getSeckillBusinessCacheList(cacheObj, SeckillGoods.class);
        // 分布式缓存中的数据为空，则查询数据库更新分布式、本地缓存
        if (Objects.isNull(seckillBusinessCacheList)) {
            seckillBusinessCacheList = tryUpdateSeckillGoodsCacheByLock(activityId, true);
        }
        // 更新本地hc
        if (Objects.nonNull(seckillBusinessCacheList) && !seckillBusinessCacheList.isRetryLater()) {
           if (localCacheUpdatelock.tryLock()) {
               try {
                   localCacheService.put(activityId, seckillBusinessCacheList);
                   logger.info("SeckillGoodsListCache|已更新本地缓存|{}", activityId);
               } finally {
                   localCacheUpdatelock.unlock();
               }
           }
         }
        return seckillBusinessCacheList;
    }
}
