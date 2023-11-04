package com.freeing.seckill.order.application.place.impl;

import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.lock.DistributedLock;
import com.freeing.seckill.common.lock.factory.DistributedLockFactory;
import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;
import com.freeing.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.application.place.SeckillPlaceOrderService;
import com.freeing.seckill.order.domain.model.entity.SeckillOrder;
import com.freeing.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
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
            SeckillOrder seckillOrder = this.buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
            // 保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 扣减数据库库存
            seckillGoodsDubboService.updateDbAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId());
            return seckillOrder.getId();
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
            throw new SeckillException(e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
