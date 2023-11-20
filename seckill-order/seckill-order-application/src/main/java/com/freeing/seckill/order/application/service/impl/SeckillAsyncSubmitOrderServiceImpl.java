package com.freeing.seckill.order.application.service.impl;

import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.model.dto.SeckillOrderSubmitDTO;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.application.model.task.SeckillOrderTask;
import com.freeing.seckill.order.application.service.base.SeckillBaseSubmitOrderServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yanggy
 */
@Service
@ConditionalOnProperty(name = "submit.order.type", havingValue = "async")
public class SeckillAsyncSubmitOrderServiceImpl extends SeckillBaseSubmitOrderServiceImpl {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        // 参数检查
        this.checkSeckillOrder(userId, seckillOrderCommand);
        // 订单任务ID
        String orderTaskId = "";
        // 构造下单任务
        SeckillOrderTask seckillOrderTask =
            new SeckillOrderTask(SeckillConstants.TOPIC_ORDER_MSG, orderTaskId, userId, seckillOrderCommand);
        // 提交订单


        return null;
    }

    @Override
    public void handlerPlaceOrderTask(SeckillOrderTask seckillOrderTask) {

    }
}
