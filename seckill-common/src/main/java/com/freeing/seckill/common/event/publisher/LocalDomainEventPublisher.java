package com.freeing.seckill.common.event.publisher;

import com.alibaba.cola.event.DomainEventI;
import com.alibaba.cola.event.EventBusI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 本地事件发布
 *
 * @author yanggy
 */
@Component
public class LocalDomainEventPublisher implements EventPublisher {
    @Autowired
    private EventBusI eventBus;

    @Override
    public void publish(DomainEventI domainEvent) {
        eventBus.fire(domainEvent);
    }
}
