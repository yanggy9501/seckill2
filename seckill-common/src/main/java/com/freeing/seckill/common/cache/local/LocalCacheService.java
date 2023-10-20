package com.freeing.seckill.common.cache.local;

/**
 * 本地缓存服务接口
 *
 * @author yanggy
 */
public interface LocalCacheService<K, V> {
    /**
     * 缓存数据
     *
     * @param key
     * @param value
     */
    void put(K key, V value);

    /**
     * 获取缓存数据
     *
     * @param key
     * @return
     */
    V getIfPresent(K key);

    /**
     * 删除缓存
     *
     * @param key
     */
    void delete(K key);
}
