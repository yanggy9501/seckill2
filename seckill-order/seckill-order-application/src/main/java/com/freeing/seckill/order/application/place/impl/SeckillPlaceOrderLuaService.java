package com.freeing.seckill.order.application.place.impl;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;
import com.freeing.seckill.common.model.message.TxMessage;
import com.freeing.seckill.common.util.id.SnowFlakeFactory;
import com.freeing.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.application.place.SeckillPlaceOrderService;
import com.freeing.seckill.order.domain.model.entity.SeckillOrder;
import com.freeing.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * lua下单扣减库存
 *
 * @author yanggy
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "lua")
public class SeckillPlaceOrderLuaService implements SeckillPlaceOrderService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderLuaService.class);

    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(),
                                                                                seckillOrderCommand.getVersion());
        // 检查商品
        this.checkSeckillGoods(seckillOrderCommand, seckillGoods);
        boolean hasException = false;

        long txNo = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
        String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));

        try {
            // 获取商品限购信息
            Object limitObj = distributedCacheService
                .getObject(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_LIMIT_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId())));
            // 如果从Redis获取到的限购信息为null，则说明商品已经下线
            if (limitObj == null) {
                throw new SeckillException(ErrorCode.GOODS_OFFLINE);
            }

            if (Integer.parseInt(String.valueOf(limitObj)) < seckillOrderCommand.getQuantity()) {
                throw new SeckillException(ErrorCode.BEYOND_LIMIT_NUM);
            }
            // lua 扣减缓存中的库存
            Long result = distributedCacheService.decrementByLua(key, seckillOrderCommand.getQuantity());
            this.checkResult(result);
        } catch (Exception e) {
            logger.error("SeckillPlaceOrderLuaService|下单异常|参数:{}|", JSONObject.toJSONString(seckillOrderCommand), e);
            hasException = true;
            distributedCacheService.incrementByLua(key, seckillOrderCommand.getQuantity());
        }
        // 事务消息
        Message<String> txMessage = this.getTxMessage(txNo, userId, SeckillConstants.PLACE_ORDER_TYPE_LUA,
            hasException, seckillOrderCommand, seckillGoods);
        // 发送事务消息
        rocketMQTemplate.sendMessageInTransaction(SeckillConstants.TOPIC_TX_MSG, txMessage, null);

        return txNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderInTransaction(TxMessage txMessage) {
        try {
            String key = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo()));
            Boolean submitTransaction = distributedCacheService.hasKey(key);
            if (BooleanUtils.isTrue(submitTransaction)) {
                logger.info("saveOrderInTransaction|已经执行过本地事务|{}", txMessage.getTxNo());
                return;
            }
            // 构建订单
            SeckillOrder seckillOrder = this.buildSeckillOrder(txMessage);
            // 保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 保存事务日志
            distributedCacheService.put(key, txMessage.getTxNo(), SeckillConstants.TX_LOG_EXPIRE_DAY, TimeUnit.DAYS);
        } catch (Exception e) {
            logger.error("saveOrderInTransaction|异常", e);
            distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())));
            this.rollbackCacheStack(txMessage);
            throw e;
        }
    }

    /**
     * 回滚缓存库存
     */
    private void rollbackCacheStack(TxMessage txMessage) {
        if (BooleanUtils.isFalse(txMessage.getException())) {
            String luaKey = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())).concat(SeckillConstants.LUA_SUFFIX);
            Long result = distributedCacheService.checkRecoverStockByLua(luaKey, SeckillConstants.TX_LOG_EXPIRE_SECONDS);

            // 已经执行过恢复缓存库存的方法
            if (NumberUtil.equals(result, SeckillConstants.CHECK_RECOVER_STOCK_HAS_EXECUTE)){
                logger.info("handlerCacheStock|已经执行过恢复缓存库存的方法|{}", JSONObject.toJSONString(txMessage));
                return;
            }
            // 只有分布式锁方式和Lua脚本方法才会扣减缓存中的库存
            String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(txMessage.getGoodsId()));
            distributedCacheService.increment(key, txMessage.getQuantity());
        }
    }

    private void checkResult(Long result){
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_NOT_EXISTS) {
            throw new SeckillException(ErrorCode.STOCK_IS_NULL);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_PARAMS_LT_ZERO){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_LT_ZERO){
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
    }
}
