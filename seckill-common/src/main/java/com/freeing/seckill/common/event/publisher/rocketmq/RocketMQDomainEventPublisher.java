package com.freeing.seckill.common.event.publisher.rocketmq;

import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.event.SeckillBaseEvent;
import com.freeing.seckill.common.event.publisher.EventPublisher;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author yanggy
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "rocketmq")
public class RocketMQDomainEventPublisher implements EventPublisher {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void publish(SeckillBaseEvent domainEvent) {
        rocketMQTemplate.send(domainEvent.getTopicEvent(), getEventMessage(domainEvent));
    }

    private Message<String> getEventMessage(SeckillBaseEvent domainEvent){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SeckillConstants.EVENT_MSG_KEY, domainEvent);
        return MessageBuilder.withPayload(jsonObject.toJSONString()).build();
    }
}
