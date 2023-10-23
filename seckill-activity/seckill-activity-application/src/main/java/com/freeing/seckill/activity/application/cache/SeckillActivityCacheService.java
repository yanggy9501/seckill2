package com.freeing.seckill.activity.application.cache;

import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;
import com.freeing.seckill.common.cache.model.SeckillBusinessCache;
import com.freeing.seckill.common.cache.service.SeckillCacheService;

/**
 * 带有缓存的秒杀活动服务接口
 *
 * @author yanggy
 */
public interface SeckillActivityCacheService extends SeckillCacheService {
    /**
     * 根据id获取活动信息
     */
    SeckillBusinessCache<SeckillActivity> getCachedSeckillActivity(Long activityId, Long version);

    /**
     * 更新缓存数据
     */
    SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activitYId, boolean doubleCheck);
}
