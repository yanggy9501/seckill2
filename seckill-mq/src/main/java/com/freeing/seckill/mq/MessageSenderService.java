package com.freeing.seckill.mq;

import com.freeing.seckill.common.model.message.TopicMessage;
import org.apache.rocketmq.client.producer.TransactionSendResult;

/**
 * 消息队列服务
 *
 * @author yanggy
 */
public interface MessageSenderService {
    /**
     * 发送消息
     * @param message 发送的消息
     */
    boolean send(TopicMessage message);

    /**
     * 发送事务消息，主要是RocketMQ
     * @param message 事务消息
     * @param arg 其他参数
     * @return 返回事务发送结果
     */
    default TransactionSendResult sendMessageInTransaction(TopicMessage message, Object arg){
        return null;
    }
}
