package com.freeing.seckill.order.domain.service.impl;

import com.alibaba.fastjson.JSON;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.event.publisher.EventPublisher;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.enums.SeckillOrderStatus;
import com.freeing.seckill.order.domain.event.SeckillOrderEvent;
import com.freeing.seckill.order.domain.model.entity.SeckillOrder;
import com.freeing.seckill.order.domain.repository.SeckillOrderRepository;
import com.freeing.seckill.order.domain.service.SeckillOrderDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author yanggy
 */
@Service
public class SeckillOrderDomainServiceImpl implements SeckillOrderDomainService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderDomainServiceImpl.class);

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private SeckillOrderRepository seckillOrderRepository;

    @Override
    public boolean saveSeckillOrder(SeckillOrder seckillOrder) {
        logger.info("saveSeckillOrder|下单|{}", JSON.toJSONString(seckillOrder));
        if (Objects.isNull(seckillOrder)) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        boolean saveSuccess = seckillOrderRepository.saveSeckillOrder(seckillOrder);
        if (saveSuccess) {
            logger.info("saveSeckillOrder|创建订单成功|{}", JSON.toJSONString(seckillOrder));
            // TODO TOPIC
            SeckillOrderEvent seckillOrderEvent =
                new SeckillOrderEvent(seckillOrder.getId(), SeckillOrderStatus.CREATED.getCode(), "");
            eventPublisher.publish(seckillOrderEvent);

        }
        return saveSuccess;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        if (Objects.isNull(userId)) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderRepository.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        if (Objects.isNull(activityId)) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderRepository.getSeckillOrderByActivityId(activityId);
    }

    @Override
    public void deleteOrder(Long orderId) {
        seckillOrderRepository.deleteOrder(orderId);
    }
}
