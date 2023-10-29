package com.freeing.seckill.order.application.event.handler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * 订单事件处理器
 *
 * @author yanggy
 */
@EventHandler
@ConditionalOnProperty(name = "message.mq.type", havingValue = "cola")
public class SeckillOrderColaEventHandler implements EventHandlerI<Response, SeckillOrderEvent> {
}
