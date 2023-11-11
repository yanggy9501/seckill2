package com.freeing.seckill.activity.domain.service.impl;

import com.alibaba.fastjson.JSON;
import com.freeing.seckill.activity.domain.event.SeckillActivityEvent;
import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;
import com.freeing.seckill.activity.domain.repository.SeckillActivityRepository;
import com.freeing.seckill.activity.domain.service.SeckillActivityDomainService;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.event.publisher.EventPublisher;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.enums.SeckillActivityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 领域层实现类
 *
 * @author yanggy
 */
@Service
public class SeckillActivityDomainServiceImpl implements SeckillActivityDomainService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillActivityDomainServiceImpl.class);

    @Value("${event.publish.type}")
    private String eventType;

    @Autowired
    private SeckillActivityRepository seckillActivityRepository;

    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public void saveSeckillActivity(SeckillActivity seckillActivity) {
        logger.info("saveSeckillActivity|发布秒杀活动|{}", JSON.toJSONString(seckillActivity));
        if (Objects.isNull(seckillActivity) || !seckillActivity.validateParams()) {
            logger.warn("saveSeckillActivity|秒杀活动参数异常|{}", JSON.toJSONString(seckillActivity));
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillActivity.setStatus(SeckillActivityStatus.PUBLISHED.getCode());
        seckillActivityRepository.saveSeckillAcivity(seckillActivity);
        logger.info("saveSeckillActivity|秒杀活动已发布|{}", seckillActivity.getId());

        // 发布事件
        SeckillActivityEvent seckillActivityEvent = new SeckillActivityEvent(seckillActivity.getId(), seckillActivity.getStatus(), getTopicEvent());
        eventPublisher.publish(seckillActivityEvent);
        logger.info("saveSeckillActivity|秒杀活动事件已发布|{}", JSON.toJSONString(seckillActivity));
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long activitId) {
        if (Objects.isNull(activitId)) {
            throw new SeckillException(ErrorCode.PASSWORD_IS_NULL);
        }
        return seckillActivityRepository.getSeckillActivityById(activitId);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return seckillActivityRepository.getSeckillActivityList(status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return seckillActivityRepository.getSeckillActivityListBetweenStartTimeAndEndtime(currentTime, status);
    }

    /**
     * 获取主题事件
     */
    private String getTopicEvent(){
        return SeckillConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ?
            SeckillConstants.TOPIC_EVENT_ROCKETMQ_ACTIVITY : SeckillConstants.TOPIC_EVENT_COLA;
    }
}
