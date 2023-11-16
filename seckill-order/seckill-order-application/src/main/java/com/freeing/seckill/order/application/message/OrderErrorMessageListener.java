package com.freeing.seckill.order.application.message;

import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.model.message.ErrorMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监听异常消息
 *
 * @author yanggy
 */
@Component
@RocketMQMessageListener(
    consumerGroup = SeckillConstants.TX_ORDER_CONSUMER_GROUP,
    topic = SeckillConstants.TOPIC_ERROR_MSG)
public class OrderErrorMessageListener implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(OrderErrorMessageListener.class);

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Override
    public void onMessage(String message) {
        logger.info("rocketmq|OrderErrorMessage|秒杀订单微服务接收并消费异常消息|{}", message);
        if (StringUtils.isEmpty(message)) {
            return;
        }
        // 删除数据库中对应的订单
    }

    private ErrorMessage getErrorMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, ErrorMessage.class);
    }
}
