package com.freeing.seckill.goods.application.service.impl;

import com.freeing.seckill.goods.application.builder.SeckillGoodsBuilder;
import com.freeing.seckill.goods.application.model.command.SeckillGoodsCommand;
import com.freeing.seckill.goods.application.service.SeckillGoodsService;
import com.freeing.seckill.common.cache.distribute.DistributedCacheService;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.dto.SeckillActivityDTO;
import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;
import com.freeing.seckill.common.model.enums.SeckillGoodsStatus;
import com.freeing.seckill.common.util.id.SnowFlakeFactory;
import com.freeing.seckill.dubbo.interfaces.activity.SeckillActivityDubboService;
import com.freeing.seckill.goods.domain.model.entity.SeckillGoods;
import com.freeing.seckill.goods.domain.service.SeckillGoodsDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author yanggy
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @DubboReference(version = "1.0.0", check = false)
    private SeckillActivityDubboService seckillActivityDubboService;

    @Autowired
    private SeckillGoodsDomainService seckillGoodsDomainService;

    @Autowired
    private DistributedCacheService distributedCacheService;

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
    public boolean updateDbAvailableStock(Integer count, Long id) {
        return seckillGoodsDomainService.updateDbAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return null;
    }
}
