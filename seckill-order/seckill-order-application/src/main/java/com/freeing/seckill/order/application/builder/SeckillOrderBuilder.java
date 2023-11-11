package com.freeing.seckill.order.application.builder;

import cn.hutool.core.bean.BeanUtil;
import com.freeing.seckill.common.builder.SeckillCommonBuilder;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.domain.model.entity.SeckillOrder;

/**
 * 订单对象转换类
 *
 * @author yanggy
 */
public class SeckillOrderBuilder extends SeckillCommonBuilder {

    public static SeckillOrder toSeckillOrder(SeckillOrderCommand seckillOrderCommand){
        if (seckillOrderCommand == null){
            return null;
        }
        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderCommand, seckillOrder);
        return seckillOrder;
    }
}