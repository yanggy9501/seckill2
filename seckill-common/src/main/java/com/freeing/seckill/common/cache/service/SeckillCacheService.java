package com.freeing.seckill.common.cache.service;

/**
 * @author yanggy
 */
public interface SeckillCacheService {
    /**
     * 构建缓存的key
     */
    String buildCacheKey(Object key);
}
