package com.freeing.seckill.order.infrastructure.mapper;

import com.freeing.seckill.order.domain.model.entity.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeckillOrderMapper {
    /**
     * 保存订单
     */
    int saveSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(@Param("userId") Long userId);

    /**
     * 根据活动id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByActivityId(@Param("activityId") Long activityId);

    /**
     * 删除订单数据
     *
     * @param orderId
     */
    void deleteOrder(Long orderId);
}
