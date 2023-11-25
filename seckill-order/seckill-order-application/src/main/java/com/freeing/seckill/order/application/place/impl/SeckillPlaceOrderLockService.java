package com.freeing.seckill.order.application.place.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.lock.DistributedLock;
import com.freeing.seckill.common.lock.factory.DistributedLockFactory;
import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;
import com.freeing.seckill.common.model.message.TxMessage;
import com.freeing.seckill.common.util.id.SnowFlakeFactory;
import com.freeing.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import com.freeing.seckill.mq.MessageSenderService;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.application.place.SeckillPlaceOrderService;
import com.freeing.seckill.order.domain.model.entity.SeckillOrder;
import com.freeing.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁下单
 *
 * @author yanggy
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "lock")
public class SeckillPlaceOrderLockService implements SeckillPlaceOrderService {
    private final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderLockService.class);

    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @Autowired
    private MessageSenderService messageSenderService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillGoodsDTO seckillGoods =
            seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品
        this.checkSeckillGoods(seckillOrderCommand, seckillGoods);
        String lockKey = SeckillConstants
            .getKey(SeckillConstants.ORDER_LOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        DistributedLock lock = distributedLockFactory.getDistributedLock(lockKey);
        // 获取内存中的库存信息
        String key = SeckillConstants
            .getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));

        boolean isDecrementCacheStock = false;
        boolean exception = false;
        long txNo = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
        try {
            // 尝试获取锁
            if (!lock.tryLock(2, 5, TimeUnit.SECONDS)) {
                throw new SeckillException(ErrorCode.RETRY_LATER);
            }
            // 查询库存信息
            Integer stock = distributedCacheService.getObject(key, Integer.class);
            if (stock == null || stock < seckillOrderCommand.getQuantity()) {
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
            // 扣减库存
            distributedCacheService.decrement(key, seckillOrderCommand.getQuantity());
            // 正常执行扣减库存缓存中库存的操作
            isDecrementCacheStock = true;
        }
        catch (Exception e) {
            // 已经扣减了缓存中的库存，则需要增加回来
            if (isDecrementCacheStock){
                distributedCacheService.increment(key, seckillOrderCommand.getQuantity());
            }
            if (e instanceof InterruptedException) {
                logger.error("SeckillPlaceOrderLockService|下单分布式锁被中断|参数 {}", JSONObject.toJSONString(seckillOrderCommand), e);
            } else {
                logger.error("SeckillPlaceOrderLockService|分布式锁下单失败|参数 {}", JSONObject.toJSONString(seckillOrderCommand), e);
            }
            exception = true;
        } finally {
            lock.unlock();
        }
        // 事务消息
        TxMessage txMessage = this.getTxMessage(SeckillConstants.TOPIC_TX_MSG, txNo, userId, SeckillConstants.PLACE_ORDER_TYPE_LOCK, exception, seckillOrderCommand, seckillGoods);
        //发送事务消息
        messageSenderService.sendMessageInTransaction(txMessage, null);
        return txNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderInTransaction(TxMessage txMessage) {
        try {
            Boolean submitTransaction = distributedCacheService.hasKey(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())));
            if (BooleanUtil.isTrue(submitTransaction)){
                logger.info("saveOrderInTransaction|已经执行过本地事务|{}", txMessage.getTxNo());
                return;
            }
            //构建订单
            SeckillOrder seckillOrder = this.buildSeckillOrder(txMessage);
            //保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            //保存事务日志
            distributedCacheService.put(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())), txMessage.getTxNo(), SeckillConstants.TX_LOG_EXPIRE_DAY, TimeUnit.DAYS);
        } catch (Exception e) {
            logger.error("saveOrderInTransaction|异常|{}", e.getMessage());
            distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())));
            this.rollbackCacheStack(txMessage);
            throw e;
        }
    }

    /**
     * 回滚缓存库存
     */
    private void rollbackCacheStack(TxMessage txMessage) {
        // 扣减过缓存库存
        if (BooleanUtil.isFalse(txMessage.getException())){
            String luaKey = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())).concat(SeckillConstants.LUA_SUFFIX);
            Long result = distributedCacheService.checkExecute(luaKey, SeckillConstants.TX_LOG_EXPIRE_SECONDS);
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
}
