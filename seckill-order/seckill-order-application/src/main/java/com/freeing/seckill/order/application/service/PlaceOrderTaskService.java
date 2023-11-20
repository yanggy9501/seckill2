package com.freeing.seckill.order.application.service;

import com.freeing.seckill.order.application.model.task.SeckillOrderTask;

/**
 * 订单任务服务
 *
 * @author yanggy
 */
public interface PlaceOrderTaskService {

    /**
     * 提交订单
     *
     * @param seckillOrderTask
     * @return
     */
    boolean submitOrderTask(SeckillOrderTask seckillOrderTask);
}
