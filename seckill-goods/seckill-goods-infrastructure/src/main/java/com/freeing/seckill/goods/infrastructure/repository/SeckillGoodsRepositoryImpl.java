package com.freeing.seckill.goods.infrastructure.repository;

import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.goods.domain.model.entity.SeckillGoods;
import com.freeing.seckill.goods.domain.repository.SeckillGoodsRepository;
import com.freeing.seckill.goods.infrastructure.mapper.SeckillGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yanggy
 */
@Repository
public class SeckillGoodsRepositoryImpl implements SeckillGoodsRepository {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public int saveSeckillGoods(SeckillGoods seckillGoods) {
        if (seckillGoods == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return 0;
    }

    @Override
    public SeckillGoods getSeckillGoodsId(Long id) {
        return seckillGoodsMapper.getSeckillGoodsId(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return null;
    }

    @Override
    public int updateStatus(Integer status, Long id) {
        return 0;
    }

    @Override
    public int updateAvailableStock(Integer count, Long id) {
        return seckillGoodsMapper.updateAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return null;
    }
}
