package com.freeing.seckill.goods.application.cache;

import com.freeing.seckill.common.cache.model.SeckillBusinessCache;
import com.freeing.seckill.common.cache.service.SeckillCacheService;
import com.freeing.seckill.goods.domain.model.entity.SeckillGoods;

import java.util.List;

/**
 * 商品缓存服务接口
 */
public interface SeckillGoodsListCacheService extends SeckillCacheService {

    /**
     * 获取缓存中的商品列表
     *
     * @param activityId
     * @param version
     * @return
     */
    SeckillBusinessCache<List<SeckillGoods>> getCachedGoodsList(Long activityId, Long version);

    /**
     * 更新缓存数据
     *
     * @param activityId
     * @param doubleCheck
     * @return
     */
    SeckillBusinessCache<List<SeckillGoods>> tryUpdateSeckillGoodsCacheByLock(Long activityId, boolean doubleCheck);
}
