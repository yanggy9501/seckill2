package com.freeing.seckill.mq.local;

import com.alibaba.cola.event.EventBusI;
import com.freeing.seckill.common.model.message.TopicMessage;
import com.freeing.seckill.mq.MessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 本地消息发送
 *
 * @author yanggy
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "cola")
public class LocalMessageSenderService implements MessageSenderService {

    @Autowired
    private EventBusI eventBus;

    @Override
    public boolean send(TopicMessage message) {
        try {
            eventBus.fire(message);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
