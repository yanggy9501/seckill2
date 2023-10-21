package com.freeing.seckill.activity.application.service;

import com.freeing.seckill.activity.application.command.SeckillActivityCommand;
import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;
import com.freeing.seckill.common.model.dto.SeckillActivityDTO;

import java.util.Date;
import java.util.List;

/**
 * @author yanggy
 */
public interface SeckillActivityService {
    /**
     * 保存活动信息
     */
    void saveSeckillActivity(SeckillActivityCommand seckillActivityCommand);

    /**
     * 活动列表
     */
    List<SeckillActivity> getSeckillActivityList(Integer status);

    /**
     * 获取正在进行中的活动列表
     */
    List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status);
    /**
     * 活动列表
     */
    List<SeckillActivityDTO> getSeckillActivityList(Integer status, Long version);

    /**
     * 获取正在进行中的活动列表
     */
    List<SeckillActivityDTO> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status, Long version);

    /**
     * 根据id获取活动信息
     */
    @Deprecated
    SeckillActivity getSeckillActivityById(Long id);

    /**
     * 获取活动信息，带有缓存
     */
    SeckillActivityDTO getSeckillActivity(Long id, Long version);

    /**
     * 修改状态
     */
    void updateStatus(Integer status, Long id);
}
