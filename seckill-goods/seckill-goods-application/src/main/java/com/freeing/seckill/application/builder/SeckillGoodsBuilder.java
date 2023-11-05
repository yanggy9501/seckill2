package com.freeing.seckill.application.builder;

import cn.hutool.core.bean.BeanUtil;
import com.freeing.seckill.application.model.command.SeckillGoodsCommand;
import com.freeing.seckill.common.builder.SeckillCommonBuilder;
import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;
import com.freeing.seckill.goods.infrastructure.model.entity.SeckillGoods;
import org.springframework.beans.BeanUtils;

/**
 * 秒杀商品转化类
 *
 * @author yanggy
 */
public class SeckillGoodsBuilder extends SeckillCommonBuilder {

    public static SeckillGoods toSeckillGoods(SeckillGoodsCommand seckillGoodsCommand) {
        if (seckillGoodsCommand == null){
            return null;
        }
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtils.copyProperties(seckillGoodsCommand, seckillGoods);
        return seckillGoods;
    }

    public static SeckillGoodsDTO toSeckillGoodsDTO(SeckillGoods seckillGoods){
        if (seckillGoods == null){
            return null;
        }
        SeckillGoodsDTO seckillGoodsDTO = new SeckillGoodsDTO();
        BeanUtil.copyProperties(seckillGoods, seckillGoodsDTO);
        return seckillGoodsDTO;
    }
}
