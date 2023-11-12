package com.freeing.seckill.goods.application.messager;

import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.model.message.TxMessage;
import com.freeing.seckill.goods.application.service.SeckillGoodsService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商品微服务事务消息
 *
 * @author yanggy
 */
@Component
@RocketMQMessageListener(
    consumerGroup = SeckillConstants.TX_GOODS_CONSUMER_GROUP,
    topic = SeckillConstants.TOPIC_TX_MSG
)
public class GoodsTxMessageListener implements RocketMQListener<String> {
    private static final Logger logger = LoggerFactory.getLogger(GoodsTxMessageListener.class);

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Override
    public void onMessage(String message) {
        logger.info("rocketmq|GoodsTxMessage|{}", message);
        if (StringUtils.isEmpty(message)) {
            return;
        }
        TxMessage txMessage = this.getTxMessage(message);
        // 订单微服务没有抛出异常，则处理库存信息
        if (BooleanUtils.isFalse(txMessage.getException())) {
            seckillGoodsService.updateAvailableStock(txMessage);
        }
    }

    private TxMessage getTxMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, TxMessage.class);
    }
}
