package com.freeing.seckill.goods.application.service;

import com.freeing.seckill.goods.application.model.command.SeckillGoodsCommand;
import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;
import com.freeing.seckill.goods.domain.model.entity.SeckillGoods;

import java.util.List;

/**
 * 商品
 *
 * @author yanggy
 */
public interface SeckillGoodsService {
    /**
     * 保存商品信息
     */
    void saveSeckillGoods(SeckillGoodsCommand seckillGoodsCommand);

    /**
     * 根据id获取商品详细信息
     */
    SeckillGoods getSeckillGoodsId(Long id);

    /**
     * 根据id获取商品详细信息（带缓存）
     */
    SeckillGoodsDTO getSeckillGoods(Long id, Long version);

    /**
     * 根据活动id获取商品列表
     */
    List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId);

    /**
     * 根据活动id从缓存中获取数据
     */
    List<SeckillGoodsDTO> getSeckillGoodsList(Long activityId, Long version);

    /**
     * 修改商品状态
     */
    void updateStatus(Integer status, Long id);

    /**
     * 扣减库存
     */
    boolean updateAvailableStock(Integer count, Long id);

    /**
     * 扣减数据库库存
     */
    boolean updateDbAvailableStock(Integer count, Long id);

    /**
     * 获取当前可用库存
     */
    Integer getAvailableStockById(Long id);
}
