package com.freeing.seckill.order.application.place.impl;

import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;
import com.freeing.seckill.common.model.message.TxMessage;
import com.freeing.seckill.common.util.id.SnowFlakeFactory;
import com.freeing.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import com.freeing.seckill.mq.MessageSenderService;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.application.place.SeckillPlaceOrderService;
import com.freeing.seckill.order.domain.model.entity.SeckillOrder;
import com.freeing.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 基于数据库下单扣减库存
 *
 * @author yanggy
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "db")
public class SeckillPlaceOrderDbService implements SeckillPlaceOrderService {
    private final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderDbService.class);

    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;

    @Autowired
    private MessageSenderService messageSenderService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @Override
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService
            .getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品信息
        this.checkSeckillGoods(seckillOrderCommand, seckillGoods);
        long txNo = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
        boolean exception = false;
        try {
            Integer availableStock = seckillGoodsDubboService.getAvailableStockById(seckillOrderCommand.getGoodsId());
            // 库存不足
            if (availableStock == null || availableStock < seckillOrderCommand.getQuantity()) {
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
        } catch (Exception e) {
            exception = true;
            logger.error("SeckillPlaceOrderDbService|下单异常|参数:{}", JSONObject.toJSONString(seckillOrderCommand), e);
        }
        // 发送事物消息
        TxMessage txMessage = this.getTxMessage(SeckillConstants.TOPIC_TX_MSG, txNo, userId,
            SeckillConstants.PLACE_ORDER_TYPE_DB, exception, seckillOrderCommand, seckillGoods);
        messageSenderService.sendMessageInTransaction(txMessage, null);
        return txNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderInTransaction(TxMessage txMessage) {
        try {
            // 检测事务是否已经执行过
            String key = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo()));
            Boolean submitTransaction = distributedCacheService.hasKey(key);
            if (BooleanUtils.isTrue(submitTransaction)) {
                logger.info("saveOrderInTransaction|本地事务已被执行过|return|{}", txMessage.getTxNo());
                return;
            }
            // 构建订单
            SeckillOrder seckillOrder = this.buildSeckillOrder(txMessage);
            // 保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 保存事务日志
            distributedCacheService.put(key, txMessage.getTxNo(), SeckillConstants.TX_LOG_EXPIRE_DAY, TimeUnit.DAYS);
        } catch (Exception e) {
            logger.error("saveOrderInTransaction|保存订单异常", e);
            distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())));
            throw e;
        }
    }
}
