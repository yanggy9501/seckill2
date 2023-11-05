package com.freeing.seckill.goods.infrastructure.event;

import com.freeing.seckill.common.event.SeckillBaseEvent;

/**
 * @author yanggy
 */
public class SeckillGoodsEvent extends SeckillBaseEvent {
    private Long activityId;

    public SeckillGoodsEvent(Long id, Long activityId, Integer status) {
        super(id, status);
        this.activityId = activityId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}