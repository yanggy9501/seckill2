package com.freeing.seckill.activity.domain.event;

import com.freeing.seckill.common.event.SeckillBaseEvent;

/**
 * @author yanggy
 */
public class SeckillActivityEvent extends SeckillBaseEvent {
    public SeckillActivityEvent(Long id, Integer status) {
        super(id, status);
    }
}
