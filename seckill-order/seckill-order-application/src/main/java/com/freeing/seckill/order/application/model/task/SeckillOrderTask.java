package com.freeing.seckill.order.application.model.task;

import cn.hutool.core.util.StrUtil;
import com.freeing.seckill.common.model.message.TopicMessage;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;

/**
 * 异步下单提交的订单任务
 *
 * @author yanggy
 */
public class SeckillOrderTask extends TopicMessage {
    private String orderTaskId;

    private Long userId;

    private SeckillOrderCommand seckillOrderCommand;

    public SeckillOrderTask(String destination, String orderTaskId,
        Long userId,
        SeckillOrderCommand seckillOrderCommand) {

        super(destination);
        this.orderTaskId = orderTaskId;
        this.userId = userId;
        this.seckillOrderCommand = seckillOrderCommand;
    }

    public String getOrderTaskId() {
        return orderTaskId;
    }

    public void setOrderTaskId(String orderTaskId) {
        this.orderTaskId = orderTaskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SeckillOrderCommand getSeckillOrderCommand() {
        return seckillOrderCommand;
    }

    public void setSeckillOrderCommand(SeckillOrderCommand seckillOrderCommand) {
        this.seckillOrderCommand = seckillOrderCommand;
    }

    public boolean isEmpty(){
        return StrUtil.isEmpty(this.getDestination())
            || StrUtil.isEmpty(orderTaskId)
            || userId == null
            || seckillOrderCommand == null;
    }
}
