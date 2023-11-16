package com.freeing.seckill.order.infrastructure.repository;

import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.order.infrastructure.mapper.SeckillOrderMapper;
import com.freeing.seckill.order.domain.model.entity.SeckillOrder;
import com.freeing.seckill.order.domain.repository.SeckillOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * 订单
 *
 * @author yanggy
 */
@Repository
public class SeckillOrderRepositoryImpl implements SeckillOrderRepository {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public boolean saveSeckillOrder(SeckillOrder seckillOrder) {
        if (Objects.isNull(seckillOrder)) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderMapper.saveSeckillOrder(seckillOrder) == 1;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        if (Objects.isNull(userId)) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderMapper.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        if (Objects.isNull(activityId)) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderMapper.getSeckillOrderByActivityId(activityId);
    }

    @Override
    public void deleteOrder(Long orderId) {
        if (Objects.isNull(orderId)) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillOrderMapper.deleteOrder(orderId);
    }
}
