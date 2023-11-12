package com.freeing.seckill.order.application.place;

import cn.hutool.core.bean.BeanUtil;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;
import com.freeing.seckill.common.model.enums.SeckillOrderStatus;
import com.freeing.seckill.common.model.message.TxMessage;
import com.freeing.seckill.common.util.id.SnowFlakeFactory;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.domain.model.entity.SeckillOrder;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 下单接口
 */
public interface SeckillPlaceOrderService {
    /**
     * 下单操作
     *
     * @param userId
     * @param seckillOrderCommand
     */
    Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand);

    /**
     *  本地事务执行保存订单操作
     *
     * @param txMessage 事务消息
     */
    void saveOrderInTransaction(TxMessage txMessage);

    /**
     * 构建订单
     */
    default SeckillOrder buildSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods){
        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderCommand, seckillOrder);
        seckillOrder.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillOrder.setGoodsName(seckillGoods.getGoodsName());
        seckillOrder.setUserId(userId);
        seckillOrder.setGoodsId(seckillGoods.getId());
        seckillOrder.setActivityPrice(seckillGoods.getActivityPrice());
        BigDecimal orderPrice = seckillGoods.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity()));
        seckillOrder.setOrderPrice(orderPrice);
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        seckillOrder.setCreateTime(new Date());
        return seckillOrder;
    }

    /**
     * 构建订单
     */
    default SeckillOrder buildSeckillOrder(TxMessage txMessage){
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(txMessage.getTxNo());
        seckillOrder.setUserId(txMessage.getUserId());
        seckillOrder.setGoodsId(txMessage.getGoodsId());
        seckillOrder.setGoodsName(txMessage.getGoodsName());
        seckillOrder.setActivityPrice(txMessage.getActivityPrice());
        seckillOrder.setQuantity(txMessage.getQuantity());
        BigDecimal orderPrice = txMessage.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity()));
        seckillOrder.setOrderPrice(orderPrice);
        seckillOrder.setActivityId(txMessage.getActivityId());
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        seckillOrder.setCreateTime(new Date());
        return seckillOrder;
    }

    /**
     * 检测商品信息
     */
    default void checkSeckillGoods(SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods) {
        if (seckillGoods == null) {
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        // 已经超出活动时间范围
        if (!seckillGoods.isInSeckilling()) {
            throw new SeckillException(ErrorCode.BEYOND_TIME);
        }
        // 商品已下架
        if (seckillGoods.isOffline()){
            throw new SeckillException(ErrorCode.GOODS_OFFLINE);
        }
        // 触发限购
        if (seckillGoods.getLimitNum() < seckillOrderCommand.getQuantity()){
            throw new SeckillException(ErrorCode.BEYOND_LIMIT_NUM);
        }
        // 库存不足
        if (seckillGoods.getAvailableStock() == null
            || seckillGoods.getAvailableStock() <= 0
            || seckillOrderCommand.getQuantity() > seckillGoods.getAvailableStock()){
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
    }

    /**
     * 事务消息
     */
    default TxMessage getTxMessage(String destination, Long txNo, Long userId, String placeOrderType, Boolean exception,
        SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods){
        // 构建事务消息
        return new TxMessage(destination, txNo, seckillOrderCommand.getGoodsId(), seckillOrderCommand.getQuantity(),
            seckillOrderCommand.getActivityId(), seckillOrderCommand.getVersion(), userId, seckillGoods.getGoodsName(),
            seckillGoods.getActivityPrice(), placeOrderType, exception);
    }
}
