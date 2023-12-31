package com.freeing.seckill.common.event.publisher;

import com.freeing.seckill.common.event.SeckillBaseEvent;

/**
 * 事件发布器
 */
public interface EventPublisher {
    /**
     * 发布事件
     */
    void publish(SeckillBaseEvent domainEvent);
}
