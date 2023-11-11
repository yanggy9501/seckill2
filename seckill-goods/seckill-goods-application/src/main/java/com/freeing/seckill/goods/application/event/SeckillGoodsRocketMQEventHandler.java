package com.freeing.seckill.goods.application.event;

import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.goods.application.cache.SeckillGoodsCacheService;
import com.freeing.seckill.goods.application.cache.SeckillGoodsListCacheService;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.util.StringUtils;
import com.freeing.seckill.goods.domain.event.SeckillGoodsEvent;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 基于RocketMQ的商品事件处理器
 *
 * @author yanggy
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "rocketmq")
@RocketMQMessageListener(
    consumerGroup = SeckillConstants.EVENT_GOODS_CONSUMER_GROUP,
    topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_GOODS
)
public class SeckillGoodsRocketMQEventHandler implements RocketMQListener<String> {
   private static final Logger logger = LoggerFactory.getLogger(SeckillGoodsRocketMQEventHandler.class);

    @Autowired
    private SeckillGoodsCacheService seckillGoodsCacheService;

    @Autowired
    private SeckillGoodsListCacheService seckillGoodsListCacheService;

    @Override
    public void onMessage(String message) {
        logger.info("rocketmq|goodEvent|接收秒杀商品事件|{}", message);
        if (StringUtils.isEmpty(message)) {
            logger.info("rocketmq|goodsEvent|接收秒杀品事件参数错误" );
            return;
        }
        SeckillGoodsEvent seckillGoodsEvent = getEventMessage(message);
        seckillGoodsCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getId(), false);
        seckillGoodsListCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getActivityId(), false);
    }

    private SeckillGoodsEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventString = jsonObject.getString(SeckillConstants.EVENT_MSG_KEY);
        return JSONObject.parseObject(eventString, SeckillGoodsEvent.class);
    }
}
