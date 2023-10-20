package com.freeing.seckill.common.cache.local.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存工厂
 *
 * @author yanggy
 */
public class LocalCacheFactory {
    public static <K, V> Cache<K, V> getLocalCache() {
        return CacheBuilder.newBuilder()
            .initialCapacity(15) // 初始化容量 15
            .concurrencyLevel(5) // 并发数
            .expireAfterWrite(10, TimeUnit.SECONDS) // 过期数据 10 秒
            .build();
    }
}
