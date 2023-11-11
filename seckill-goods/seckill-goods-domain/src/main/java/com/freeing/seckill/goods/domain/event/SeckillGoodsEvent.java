package com.freeing.seckill.goods.domain.event;

import com.freeing.seckill.common.event.SeckillBaseEvent;

/**
 * 秒杀商品事件
 *
 * @author yanggy
 */
public class SeckillGoodsEvent extends SeckillBaseEvent {
    private Long activityId;

    public SeckillGoodsEvent(Long id, Long activityId, Integer status, String topicEvent) {
        super(id, status, topicEvent);
        this.activityId = activityId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}