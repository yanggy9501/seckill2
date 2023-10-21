package com.freeing.seckill.activity.application.event.handler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import com.freeing.seckill.activity.application.cache.SeckillActivityCacheService;
import com.freeing.seckill.activity.domain.event.SeckillActivityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * 活动事件监听器、处理器
 *
 * @author yanggy
 */
@EventHandler
public class SeckillActivityEventHandler implements EventHandlerI<Response, SeckillActivityEvent> {
    private static final Logger logger = LoggerFactory.getLogger(SeckillActivityEventHandler.class);

    @Autowired
    private SeckillActivityCacheService seckillActivityCacheService;

    @Override
    public Response execute(SeckillActivityEvent seckillActivityEvent) {
        logger.info("SeckillActivityEventHandler#execute|接收活动事件|{}", JSON.toJSONString(seckillActivityEvent));
        if (Objects.isNull(seckillActivityEvent)) {
            return Response.buildSuccess();
        }
        seckillActivityCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getId(), false);
        return Response.buildSuccess();
    }
}
