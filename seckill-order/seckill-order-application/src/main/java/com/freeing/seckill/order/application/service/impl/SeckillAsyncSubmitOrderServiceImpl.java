package com.freeing.seckill.order.application.service.impl;

import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.dto.SeckillOrderSubmitDTO;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.application.model.task.SeckillOrderTask;
import com.freeing.seckill.order.application.service.OrderTaskGenerateService;
import com.freeing.seckill.order.application.service.PlaceOrderTaskService;
import com.freeing.seckill.order.application.service.base.SeckillBaseSubmitOrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @author yanggy
 */
@Service
@ConditionalOnProperty(name = "submit.order.type", havingValue = "async")
public class SeckillAsyncSubmitOrderServiceImpl extends SeckillBaseSubmitOrderServiceImpl {

    @Autowired
    private PlaceOrderTaskService placeOrderTaskService;

    @Autowired
    private OrderTaskGenerateService orderTaskGenerateService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        // 参数检查
        this.checkSeckillOrder(userId, seckillOrderCommand);
        // 订单任务ID
        String orderTaskId = orderTaskGenerateService.generatePlaceOrderTaskId(userId, seckillOrderCommand.getGoodsId());
        // 构造下单任务
        SeckillOrderTask seckillOrderTask =
            new SeckillOrderTask(SeckillConstants.TOPIC_ORDER_MSG, orderTaskId, userId, seckillOrderCommand);
        // 提交订单
        boolean isSubmit = placeOrderTaskService.submitOrderTask(seckillOrderTask);
        if (!isSubmit) {
            throw new SeckillException(ErrorCode.ORDER_FAILED);
        }
        return new SeckillOrderSubmitDTO(orderTaskId, seckillOrderCommand.getGoodsId(), SeckillConstants.TYPE_TASK);
    }

    @Override
    public void handlerPlaceOrderTask(SeckillOrderTask seckillOrderTask) {
        Long orderId = seckillPlaceOrderService.placeOrder(seckillOrderTask.getUserId(), seckillOrderTask.getSeckillOrderCommand());
        if (orderId != null) {
            String key = SeckillConstants.getKey(SeckillConstants.ORDER_TASK_ORDER_ID_KEY, seckillOrderTask.getOrderTaskId());
            distributedCacheService.put(key, orderId, SeckillConstants.ORDER_TASK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
    }
}
