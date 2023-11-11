package com.freeing.seckill.goods.application.cache;

import com.freeing.seckill.common.cache.model.SeckillBusinessCache;
import com.freeing.seckill.common.cache.service.SeckillCacheService;
import com.freeing.seckill.goods.domain.model.entity.SeckillGoods;

/**
 * 商品缓存服务接口
 */
public interface SeckillGoodsCacheService extends SeckillCacheService {

    /**
     * 获取商品信息
     *
     * @param goodsId 商品ID
     * @param version 版本
     * @return
     */
    SeckillBusinessCache<SeckillGoods> getSeckillGoods(Long goodsId, Long version);

    /**
     * 更新缓存
     *
     * @param goodsId 商品ID
     * @param doubleCheck
     * @return
     */
    SeckillBusinessCache<SeckillGoods> tryUpdateSeckillGoodsCacheByLock(Long goodsId, boolean doubleCheck);
}
