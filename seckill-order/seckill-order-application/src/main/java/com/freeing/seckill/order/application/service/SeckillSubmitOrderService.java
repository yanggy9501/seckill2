package com.freeing.seckill.order.application.service;

import com.freeing.seckill.common.model.dto.SeckillOrderSubmitDTO;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.application.model.task.SeckillOrderTask;

/**
 * 订单提交服务接口
 */
public interface SeckillSubmitOrderService {
    /**
     * 保存订单
     */
    SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand);

    /**
     *  处理订单任务
     *
     * @param seckillOrderTask
     */
    default void handlerPlaceOrderTask(SeckillOrderTask seckillOrderTask) {

    }

    /**
     * 实现基础校验功能
     */
    default void checkSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand){

    }
}
