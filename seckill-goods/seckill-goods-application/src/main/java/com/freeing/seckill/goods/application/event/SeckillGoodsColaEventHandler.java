package com.freeing.seckill.goods.application.event;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import com.freeing.seckill.goods.application.cache.SeckillGoodsCacheService;
import com.freeing.seckill.goods.application.cache.SeckillGoodsListCacheService;
import com.freeing.seckill.goods.domain.event.SeckillGoodsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 基于RocketMQ的商品事件处理器
 *
 * @author yanggy
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "cola")
public class SeckillGoodsColaEventHandler implements EventHandlerI<Response, SeckillGoodsEvent> {
   private static final Logger logger = LoggerFactory.getLogger(SeckillGoodsColaEventHandler.class);

    @Autowired
    private SeckillGoodsCacheService seckillGoodsCacheService;

    @Autowired
    private SeckillGoodsListCacheService seckillGoodsListCacheService;
    @Override
    public Response execute(SeckillGoodsEvent seckillGoodsEvent) {
        logger.info("cola|goodsEvent|接收秒杀活动|{}", JSON.toJSONString(seckillGoodsEvent));
        if (Objects.isNull(seckillGoodsEvent)) {
            logger.info("cola|goodsEvent|接收秒杀品事件参数错误");
            return Response.buildSuccess();
        }
        seckillGoodsCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getId(), false);
        seckillGoodsListCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getActivityId(), false);
        return Response.buildSuccess();
    }
}
