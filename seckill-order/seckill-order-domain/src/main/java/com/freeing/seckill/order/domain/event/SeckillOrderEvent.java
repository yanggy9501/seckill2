package com.freeing.seckill.order.domain.event;

import com.freeing.seckill.common.event.SeckillBaseEvent;

/**
 * @author yanggy
 */
public class SeckillOrderEvent extends SeckillBaseEvent {
    /**
     *
     * @param id 订单ID
     * @param status 订单状态
     */
    public SeckillOrderEvent(Long id, Integer status) {
        super(id, status);
    }
}
