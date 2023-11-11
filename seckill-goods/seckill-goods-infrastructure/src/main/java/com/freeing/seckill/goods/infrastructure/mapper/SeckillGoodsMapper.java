package com.freeing.seckill.goods.infrastructure.mapper;

import com.freeing.seckill.goods.domain.model.entity.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SeckillGoodsMapper {
    /**
     * 保存商品信息
     */
    int saveSeckillGoods(SeckillGoods seckillGoods);

    SeckillGoods getSeckillGoodsId(Long id);

    int updateAvailableStock(@Param("count") Integer count, @Param("id") Long id);
}
