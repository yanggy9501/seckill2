package com.freeing.seckill.activity.domain.repository;

import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;

import java.util.Date;
import java.util.List;

public interface SeckillActivityRepository {

    /**
     * 保存活动信息
     *
     * @param seckillActivity
     * @return
     */
    int saveSeckillAcivity(SeckillActivity seckillActivity);

    /**
     * 活动列表
     */
    List<SeckillActivity> getSeckillActivityList(Integer status);

    /**
     * 获取正在进行中的活动列表
     *
     * @param currentTime
     * @param status
     * @return
     */
    List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndtime(Date currentTime, Integer status);

    /**
     * 根据id获取活动信息
     *
     * @param id
     * @return
     */
    SeckillActivity getSeckillActivityById(Long id);

    /**
     * 修改状态
     *
     * @param status
     * @param id
     * @return
     */
    int updateStatus(Integer status, Long id);
}
