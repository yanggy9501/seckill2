package com.freeing.seckill.order.application.message;

import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.order.application.model.task.SeckillOrderTask;
import com.freeing.seckill.order.application.service.SeckillSubmitOrderService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 订单任务监听类
 *
 * @author yanggy
 */
@Component
@ConditionalOnProperty(name = "submit.order.type", havingValue = "async")
@RocketMQMessageListener(
    consumerGroup = SeckillConstants.SUBMIT_ORDER_CONSUMER_GROUP,
    topic = SeckillConstants.TOPIC_ORDER_MSG
)
public class OrderTaskConsumerListener implements RocketMQListener<String> {
    private static final Logger logger = LoggerFactory.getLogger(OrderTaskConsumerListener.class);

    @Autowired
    private SeckillSubmitOrderService seckillSubmitOrderService;

    @Override
    public void onMessage(String message) {
        logger.info("onMessage|秒杀订单微服务接收异步订单任务消息：{}", message);
        if (StringUtils.isEmpty(message)) {
            logger.info("onMessage|秒杀订单微服务接收异步订单任务消息为空：{}", message);
            return;
        }

        SeckillOrderTask seckillOrderTask = getTaskMessage(message);
        if (seckillOrderTask.isEmpty()){
            logger.info("onMessage|秒杀订单微服务接收异步订单任务消息转换成任务对象为空{}", message);
            return;
        }

        logger.info("onMessage|开始处理下单任务:{}", seckillOrderTask.getOrderTaskId());
        seckillSubmitOrderService.handlerPlaceOrderTask(seckillOrderTask);
    }

    private SeckillOrderTask getTaskMessage(String msg) {
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, SeckillOrderTask.class);
    }
}
