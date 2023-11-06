package com.freeing.seckill.common.event.publisher.cola;

import com.alibaba.cola.event.EventBusI;
import com.freeing.seckill.common.event.SeckillBaseEvent;
import com.freeing.seckill.common.event.publisher.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 本地事件发布
 *
 * @author yanggy
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "cola")
public class LocalDomainEventPublisher implements EventPublisher {
    @Autowired
    private EventBusI eventBus;

    @Override
    public void publish(SeckillBaseEvent domainEvent) {
        eventBus.fire(domainEvent);
    }
}
