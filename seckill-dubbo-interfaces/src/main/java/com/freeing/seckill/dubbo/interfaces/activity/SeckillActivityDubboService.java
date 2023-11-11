package com.freeing.seckill.dubbo.interfaces.activity;

import com.freeing.seckill.common.model.dto.SeckillActivityDTO;

/**
 * 活动相关的Dubbo服务
 *
 * @author yanggy
 */
public interface SeckillActivityDubboService {
    /**
     * 获取活动信息
     */
    SeckillActivityDTO getSeckillActivity(Long id, Long version);

}
