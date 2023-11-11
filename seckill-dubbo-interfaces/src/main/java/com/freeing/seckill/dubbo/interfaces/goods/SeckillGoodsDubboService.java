package com.freeing.seckill.dubbo.interfaces.goods;

import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;

/**
 * 商品 Dubbo 服务接口
 */
public interface SeckillGoodsDubboService {

    /**
     * 根据id和版本号获取商品详情
     */
    SeckillGoodsDTO getSeckillGoods(Long goodsId, Long version);

    /**
     * 根据商品id获取可用库存
     */
    Integer getAvailableStockById(Long goodsId);

    /**
     * 扣减数据库库存
     */
    boolean updateDbAvailableStock(Integer quantity, Long goodsId);
}
