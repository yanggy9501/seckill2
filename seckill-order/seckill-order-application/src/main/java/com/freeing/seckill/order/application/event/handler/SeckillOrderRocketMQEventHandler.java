package com.freeing.seckill.order.application.event.handler;

import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.util.StringUtils;
import com.freeing.seckill.order.domain.event.SeckillOrderEvent;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 基于RocketMQ的订单事件处理器
 *
 * @author yanggy
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "rocketmq")
@RocketMQMessageListener(
    consumerGroup = SeckillConstants.EVENT_ORDER_CONSUMER_GROUP,
    topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_ORDER
)
public class SeckillOrderRocketMQEventHandler implements RocketMQListener<String> {
    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderRocketMQEventHandler.class);

    @Override
    public void onMessage(String message) {
        logger.info("rocketmq|orderEvent|接收订单事件");
        if (StringUtils.isEmpty(message)) {
            logger.info("rocketmq|orderEvent|接收参数为空");
            return;
        }
        SeckillOrderEvent seckillOrderEvent = getEventMessage(message);

    }

    private SeckillOrderEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.EVENT_MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillOrderEvent.class);
    }
}
