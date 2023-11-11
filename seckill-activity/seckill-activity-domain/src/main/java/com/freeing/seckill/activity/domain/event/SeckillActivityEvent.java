package com.freeing.seckill.activity.domain.event;

import com.freeing.seckill.common.event.SeckillBaseEvent;

/**
 * 秒杀活动事件
 *
 * @author yanggy
 */
public class SeckillActivityEvent extends SeckillBaseEvent {
    public SeckillActivityEvent(Long id, Integer status, String topicEvent) {
        super(id, status, topicEvent);
    }
}
