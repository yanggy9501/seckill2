package com.freeing.seckill.activity.domain.service;

import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;

import java.util.Date;
import java.util.List;

public interface SeckillActivityDomainService {
    /**
     * 保存活动信息
     *
     * @param seckillActivity
     */
    void saveSeckillActivity(SeckillActivity seckillActivity);

    /**
     * 根据id获取活动信息
     */
    SeckillActivity getSeckillActivityById(Long activitId);

    /**
     * 获取活动列表
     *
     * @param status 活动状态
     * @return
     */
    List<SeckillActivity> getSeckillActivityList(Integer status);

    /**
     * 获取正在进行中的活动列表
     */
    List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status);
}
