package com.freeing.seckill.common.cache.local.guava;

import com.freeing.seckill.common.cache.local.LocalCacheService;
import com.google.common.cache.Cache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 基于Guava实现的本地缓存
 *
 * @author yanggy
 */
@Component
@ConditionalOnProperty(name = "local.cache.type", havingValue = "guava")
public class GuavaLocalCacheService<K, V> implements LocalCacheService<K, V> {
    /**
     * 本地缓存
     */
    private final Cache<K, V> CACHE = LocalCacheFactory.getLocalCache();

    @Override
    public void put(K key, V value) {
        CACHE.put(key, value);
    }

    @Override
    public V getIfPresent(K key) {
        return CACHE.getIfPresent(key);
    }

    @Override
    public void delete(K key) {
        CACHE.invalidate(key);
    }
}
