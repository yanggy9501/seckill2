package com.freeing.seckill.order.application.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.cache.local.LocalCacheService;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.lock.DistributedLock;
import com.freeing.seckill.common.lock.factory.DistributedLockFactory;
import com.freeing.seckill.mq.MessageSenderService;
import com.freeing.seckill.order.application.model.task.SeckillOrderTask;
import com.freeing.seckill.order.application.service.PlaceOrderTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 订单任务服务实现类
 *
 * @author yanggy
 */
@Service
public class PlaceOrderTaskServiceImpl implements PlaceOrderTaskService {
    private static final Logger logger = LoggerFactory.getLogger(PlaceOrderTaskServiceImpl.class);

    private final Lock lock = new ReentrantLock();

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private LocalCacheService<Long, Integer> localCacheService;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Value("${submit.order.token.multiple:1.5}")
    private Double multiple;

    @Autowired
    private MessageSenderService messageSenderService;

    @Override
    public boolean submitOrderTask(SeckillOrderTask seckillOrderTask) {
        if (Objects.isNull(seckillOrderTask)) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        // 检测是否执行过
        String taskKey = SeckillConstants.getKey(SeckillConstants.ORDER_TASK_ID_KEY, seckillOrderTask.getOrderTaskId());
        Long result = distributedCacheService.checkExecute(taskKey, SeckillConstants.ORDER_TASK_EXPIRE_SECONDS);
        // 已经执行过则恢复缓存的方法
        if (NumberUtil.equals(result, SeckillConstants.CHECK_RECOVER_STOCK_HAS_EXECUTE)) {
            throw new SeckillException(ErrorCode.REDUNDANT_SUBMIT);
        }

        // 获取下单许可
        Long goodsId = seckillOrderTask.getSeckillOrderCommand().getGoodsId();
        Integer availableOrderTokens = this.getAvailableOrderTokens(goodsId);
        // 已经没有许可
        if (availableOrderTokens == null || availableOrderTokens <= 0) {
            throw new SeckillException(ErrorCode.ORDER_TOKENS_NOT_AVAILABLE);
        }

        // 获取许可
        if (!this.takeOrderToken(goodsId)){
            logger.info("submitOrderTask|获取下单许可失败|{},{}", seckillOrderTask.getUserId(), seckillOrderTask.getOrderTaskId());
            throw new SeckillException(ErrorCode.ORDER_TOKENS_NOT_AVAILABLE);
        }
        // 发送消息
        boolean sendSuccess = messageSenderService.send(seckillOrderTask);
        if (!sendSuccess){
            logger.info("submitOrderTask|下单任务提交失败|{},{}", seckillOrderTask.getUserId(), seckillOrderTask.getOrderTaskId());
            // 恢复下单许可
            this.recoverOrderToken(goodsId);
            // 清除是否被执行过的数据
            distributedCacheService.delete(taskKey);
        }
        return sendSuccess;
    }

    /**
     * 恢复下单许可
     */
    private boolean recoverOrderToken(Long goodsId){
        Long result = distributedCacheService.recoverOrderToken(SeckillConstants.getKey(SeckillConstants.ORDER_TASK_AVAILABLE_TOKENS_KEY, String.valueOf(goodsId)));
        if (result == null){
            return false;
        }
        if (result == SeckillConstants.LUA_RESULT_NOT_EXECUTE){
            this.refreshLatestAvailableTokens(goodsId);
            return true;
        }
        return result == SeckillConstants.LUA_RESULT_EXECUTE_TOKEN_SUCCESS;
    }

    /**
     * TODO
     * 获取下单许可，需要重点理解为何要循环三次处理业务
     *
     * @param goodsId
     * @return
     */
    private boolean takeOrderToken(Long goodsId) {
        for (int i = 0; i < 3; i++) {
            Long result = distributedCacheService.takeOrderToken(SeckillConstants.getKey(SeckillConstants.ORDER_TASK_AVAILABLE_TOKENS_KEY, String.valueOf(goodsId)));
            if (result == null){
                return false;
            }
            if (result == SeckillConstants.LUA_RESULT_NOT_EXECUTE){
                this.refreshLatestAvailableTokens(goodsId);
                continue;
            }
            return result == SeckillConstants.LUA_RESULT_EXECUTE_TOKEN_SUCCESS;
        }
        return false;
    }

    private Integer getAvailableOrderTokens(Long goodsId) {
        Integer availableOrderTokens = localCacheService.getIfPresent(goodsId);
        if (availableOrderTokens != null) {
            return availableOrderTokens;
        }
        return refreshLocalAvailableTokens(goodsId);
    }

    /**
     * 刷新本地缓存可用的下单许可，注意DoubleCheck
     */
    private Integer refreshLocalAvailableTokens(Long goodsId) {
        Integer availableOrderTokens = localCacheService.getIfPresent(goodsId);
        if (availableOrderTokens != null) {
            return availableOrderTokens;
        }
        String availableTokensKey = SeckillConstants.getKey(SeckillConstants.ORDER_TASK_AVAILABLE_TOKENS_KEY, String.valueOf(goodsId));
        Integer latestAvailableOrderTokens = distributedCacheService.getObject(availableTokensKey, Integer.class);
        if (latestAvailableOrderTokens != null) {
            // 更新本地缓存
            if (lock.tryLock()) {
                try {
                    localCacheService.put(goodsId, latestAvailableOrderTokens);
                } finally {
                    lock.unlock();
                }
            }
            return latestAvailableOrderTokens;
        }

        return refreshLatestAvailableTokens(goodsId);
    }

    /**
     * 刷新分布式缓存的下单许可，double check
     */
    private Integer refreshLatestAvailableTokens(Long goodsId) {
        String lockKey = SeckillConstants.getKey(SeckillConstants.LOCK_REFRESH_LATEST_AVAILABLE_TOKENS_KEY, String.valueOf(goodsId));
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);
        try {
            boolean isLock = distributedLock.tryLock();
            if (!isLock) {
                return null;
            }
            // 本地缓存已经存在数据
            Integer availableOrderTokens = localCacheService.getIfPresent(goodsId);
            if (availableOrderTokens != null){
                return availableOrderTokens;
            }

            // 获取分布式缓存数据
            String availableTokensKey = SeckillConstants.getKey(SeckillConstants.ORDER_TASK_AVAILABLE_TOKENS_KEY, String.valueOf(goodsId));
            Integer latestAvailableOrderTokens = distributedCacheService.getObject(availableTokensKey, Integer.class);
            // 分布式缓存中存在数据，设置到本地缓存，并且返回数据
            if (latestAvailableOrderTokens != null){
                localCacheService.put(goodsId, latestAvailableOrderTokens);
                return latestAvailableOrderTokens;
            }

            // 本地缓存和分布式缓存都没有数据，获取商品的库存数据
            String goodsKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(goodsId));
            // 获取商品库存
            Integer availableStock = distributedCacheService.getObject(goodsKey, Integer.class);
            if (availableStock == null || availableStock <= 0){
                return null;
            }
            // 根据配置的比例计算下单许可
            latestAvailableOrderTokens = (int) Math.ceil(availableStock * multiple);
            distributedCacheService.put(availableTokensKey, latestAvailableOrderTokens, SeckillConstants.ORDER_TASK_EXPIRE_SECONDS, TimeUnit.SECONDS);
            localCacheService.put(goodsId, latestAvailableOrderTokens);
            return latestAvailableOrderTokens;
        } catch (Exception e) {
            logger.error("refreshLatestAvailableTokens|刷新下单许可失败:{}", goodsId, e);
        } finally {
            distributedLock.unlock();
        }
        return null;
    }
}
