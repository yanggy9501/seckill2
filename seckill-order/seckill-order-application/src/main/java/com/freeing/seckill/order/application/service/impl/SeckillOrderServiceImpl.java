package com.freeing.seckill.order.application.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.model.message.ErrorMessage;
import com.freeing.seckill.order.application.service.SeckillOrderService;
import com.freeing.seckill.order.domain.model.entity.SeckillOrder;
import com.freeing.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单业务
 *
 * @author yanggy
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderServiceImpl.class);

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        return null;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        return null;
    }

    /**
     * 下单扣减库存失败，删除订单
     *
     * @param errorMessage mq 订单的错误消息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(ErrorMessage errorMessage) {
        // 检查：保存订单成功并且提交过事务，才能清理订单 @see com.freeing.seckill.order.application.service.SeckillSubmitOrderService.saveSeckillOrder
        String key = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(errorMessage.getTxNo()));
        Boolean exists = distributedCacheService.hasKey(key);
        if (BooleanUtils.isFalse(exists)) {
            logger.info("deleteOrder|订单微服务为执行本地事务|{}", errorMessage.getTxNo());
            return;
        }
        seckillOrderDomainService.deleteOrder(errorMessage.getTxNo());
        this.handlerCacheStock(errorMessage);
    }

    /**
     * 处理缓存库存
     */
    private void handlerCacheStock(ErrorMessage errorMessage) {
        // 订单微服务之前未抛出异常，说明已经扣减了缓存中的库存，此时需要将缓存中的库存增加回来
        if (BooleanUtils.isFalse(errorMessage.getException())) {
            String luaKey = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY,
                String.valueOf(errorMessage.getTxNo())).concat(SeckillConstants.LUA_SUFFIX);
            Long result = distributedCacheService.checkExecute(luaKey, SeckillConstants.TX_LOG_EXPIRE_SECONDS);
            // 已经执行过恢复缓存库存的方法
            if (NumberUtil.equals(result, SeckillConstants.CHECK_RECOVER_STOCK_HAS_EXECUTE)){
                logger.info("handlerCacheStock|已经执行过恢复缓存库存的方法|{}", JSONObject.toJSONString(errorMessage));
                return;
            }
            // 只有分布式锁方式和Lua脚本方法才会扣减缓存中的库存
            String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(errorMessage.getGoodsId()));
            // 分布式锁方式
            logger.info("handlerCacheStock|回滚缓存库存|{}", JSONObject.toJSONString(errorMessage));
            if (SeckillConstants.PLACE_ORDER_TYPE_LOCK.equalsIgnoreCase(errorMessage.getPlaceOrderType())){
                distributedCacheService.increment(key, errorMessage.getQuantity());
            }
            else if (SeckillConstants.PLACE_ORDER_TYPE_LUA.equalsIgnoreCase(errorMessage.getPlaceOrderType())){  // Lua方式
                distributedCacheService.incrementByLua(key, errorMessage.getQuantity());
            }
        }
    }
}
