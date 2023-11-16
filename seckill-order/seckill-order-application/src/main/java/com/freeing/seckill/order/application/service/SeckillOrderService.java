package com.freeing.seckill.order.application.service;

import com.freeing.seckill.common.model.message.ErrorMessage;
import com.freeing.seckill.order.domain.model.entity.SeckillOrder;

import java.util.List;

/**
 * 订单服务
 */
public interface SeckillOrderService {

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    /**
     * 根据活动id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

    /**
     * 删除订单
     *
     * @param errorMessage mq 订单的错误消息
     */
    void deleteOrder(ErrorMessage errorMessage);
}
