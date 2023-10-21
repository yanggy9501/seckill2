package com.freeing.seckill.activity.domain.service;

import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;

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
}
