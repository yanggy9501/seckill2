package com.freeing.seckill.order.application.event.handler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import com.freeing.seckill.order.domain.event.SeckillOrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * 订单事件处理器
 *
 * @author yanggy
 */
@EventHandler
@ConditionalOnProperty(name = "message.mq.type", havingValue = "cola", matchIfMissing = true)
public class SeckillOrderEventHandler implements EventHandlerI<Response, SeckillOrderEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderEventHandler.class);

    @Override
    public Response execute(SeckillOrderEvent seckillOrderEvent) {
        logger.info("SeckillOrderEvent|监听订单事件|{}", JSON.toJSONString(seckillOrderEvent));
        if (seckillOrderEvent.getId() == null) {
            logger.info("orderEvent|订单参数错误|order id null");
            return Response.buildSuccess();
        }
        return Response.buildSuccess();
    }
}
