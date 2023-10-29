package com.freeing.seckill.order.domain.event;

import com.freeing.seckill.common.event.SeckillBaseEvent;

/**
 * @author yanggy
 */
public class SeckillOrderEvent extends SeckillBaseEvent {
    public SeckillOrderEvent(Long id, Integer status) {
        super(id, status);
    }
}
