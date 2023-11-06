package com.freeing.seckill.activity.application.event.handler;

import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.activity.application.cache.SeckillActivityCacheService;
import com.freeing.seckill.activity.application.cache.SeckillActivityListCacheService;
import com.freeing.seckill.activity.domain.event.SeckillActivityEvent;
import com.freeing.seckill.common.constants.SeckillConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 接收rocketmq事件消息
 *
 * @author yanggy
 */
@Component
@RocketMQMessageListener(
    consumerGroup = SeckillConstants.EVENT_ACTIVITY_CONSUMER_GROUP,
    topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_ACTIVITY)
@ConditionalOnProperty(name = "event.publish.type", havingValue = "rocketmq")
public class SeckillActivityRocketMQEventHandler implements RocketMQListener<String> {
    private static final Logger logger = LoggerFactory.getLogger(SeckillActivityRocketMQEventHandler.class);

    @Autowired
    private SeckillActivityCacheService seckillActivityCacheService;

    @Autowired
    private SeckillActivityListCacheService seckillActivityListCacheService;

    @Override
    public void onMessage(String message) {
        logger.info("rocketmq|activityEvent|接收活动事件|{}", message);
        if (StringUtils.isEmpty(message)) {
            logger.info("rocketmq|activityEvent|事件参数错误" );
            return;
        }
        SeckillActivityEvent seckillActivityEvent = getEventMessage(message);
        seckillActivityCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getId(), false);
        seckillActivityListCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getStatus(), false);
    }

    private SeckillActivityEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.EVENT_MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillActivityEvent.class);
    }
}
