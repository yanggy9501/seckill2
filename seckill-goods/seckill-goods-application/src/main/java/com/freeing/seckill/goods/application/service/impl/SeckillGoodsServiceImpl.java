package com.freeing.seckill.goods.application.service.impl;

import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.dto.SeckillActivityDTO;
import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;
import com.freeing.seckill.common.model.enums.SeckillGoodsStatus;
import com.freeing.seckill.common.model.message.ErrorMessage;
import com.freeing.seckill.common.model.message.TxMessage;
import com.freeing.seckill.common.util.id.SnowFlakeFactory;
import com.freeing.seckill.dubbo.interfaces.activity.SeckillActivityDubboService;
import com.freeing.seckill.goods.application.builder.SeckillGoodsBuilder;
import com.freeing.seckill.goods.application.model.command.SeckillGoodsCommand;
import com.freeing.seckill.goods.application.service.SeckillGoodsService;
import com.freeing.seckill.goods.domain.model.entity.SeckillGoods;
import com.freeing.seckill.goods.domain.service.SeckillGoodsDomainService;
import com.freeing.seckill.mq.MessageSenderService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author yanggy
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillGoodsServiceImpl.class);

    @DubboReference(version = "1.0.0", check = false)
    private SeckillActivityDubboService seckillActivityDubboService;

    @Autowired
    private SeckillGoodsDomainService seckillGoodsDomainService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private MessageSenderService messageSenderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSeckillGoods(SeckillGoodsCommand seckillGoodsCommand) {
        if (Objects.isNull(seckillGoodsCommand)) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillActivityDTO seckillActivity = seckillActivityDubboService
            .getSeckillActivity(seckillGoodsCommand.getActivityId(), seckillGoodsCommand.getVersion());
        if (Objects.isNull(seckillActivity)) {
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }

        SeckillGoods seckillGoods = SeckillGoodsBuilder.toSeckillGoods(seckillGoodsCommand);
        seckillGoods.setStartTime(seckillActivity.getStartTime());
        seckillGoods.setEndTime(seckillActivity.getEndTime());
        seckillGoods.setAvailableStock(seckillGoodsCommand.getInitialStock());
        seckillGoods.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillGoods.setStatus(SeckillGoodsStatus.PUBLISHED.getCode());
        // 将商品的库存同步到Redis
        distributedCacheService.put(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX,
            String.valueOf(seckillGoods.getId())),
            seckillGoods.getAvailableStock());
        // 商品限购同步到Redis
        distributedCacheService.put(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_LIMIT_KEY_PREFIX,
            String.valueOf(seckillGoods.getId())),
            seckillGoods.getLimitNum());
        seckillGoodsDomainService.saveSeckillGoods(seckillGoods);
    }

    @Override
    public SeckillGoods getSeckillGoodsId(Long id) {
        return null;
    }

    @Override
    public SeckillGoodsDTO getSeckillGoods(Long id, Long version) {
        if (Objects.isNull(id)) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }

        SeckillGoods seckillGoodsId = seckillGoodsDomainService.getSeckillGoodsId(id);
        SeckillGoodsDTO seckillGoodsDTO = new SeckillGoodsDTO();
        BeanUtils.copyProperties(seckillGoodsId, seckillGoodsDTO);
        return seckillGoodsDTO;
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return null;
    }

    @Override
    public List<SeckillGoodsDTO> getSeckillGoodsList(Long activityId, Long version) {
        return null;
    }

    @Override
    public void updateStatus(Integer status, Long id) {

    }

    @Override
    public boolean updateAvailableStock(Integer count, Long id) {
        return false;
    }

    @Override
    public boolean updateAvailableStock(TxMessage txMessage) {
        String key = SeckillConstants.getKey(SeckillConstants.GOODS_TX_KEY, String.valueOf(txMessage.getTxNo()));
        Boolean decrementAlready= distributedCacheService.hasKey(key);
        if (BooleanUtils.isTrue(decrementAlready)) {
            logger.info("updateAvailableStock|txMessage|秒杀商品微服务扣减库存|{}", txMessage.getTxNo());
            return true;
        }
        boolean isUpdate = false;
        try {
            isUpdate = seckillGoodsDomainService.updateAvailableStock(txMessage.getQuantity(), txMessage.getGoodsId());
            if (isUpdate) {
                distributedCacheService.put(key, txMessage.getTxNo(), SeckillConstants.TX_LOG_EXPIRE_DAY, TimeUnit.DAYS);
            } else {
                // 扣减库存失败，发送消息通知订单微服务
                messageSenderService.send(getErrorMessage(txMessage));
            }
        } catch (Exception e) {
            isUpdate = false;
            logger.error("updateAvailableStock|扣减库存异常|{}",txMessage.getTxNo(), e);
            // 发送失败消息给订单微服务
            messageSenderService.send(getErrorMessage(txMessage));
        }
        return isUpdate;
    }

    @Override
    public boolean updateDbAvailableStock(Integer count, Long id) {
        return seckillGoodsDomainService.updateDbAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return null;
    }

    private ErrorMessage getErrorMessage(TxMessage txMessage){
        return new ErrorMessage(
            SeckillConstants.TOPIC_ERROR_MSG,
            txMessage.getTxNo(),
            txMessage.getGoodsId(),
            txMessage.getQuantity(),
            txMessage.getPlaceOrderType(),
            txMessage.getException());
    }
}
