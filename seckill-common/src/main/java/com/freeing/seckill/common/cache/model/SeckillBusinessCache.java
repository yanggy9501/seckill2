package com.freeing.seckill.common.cache.model;

import com.freeing.seckill.common.cache.model.base.SeckillCommonCache;

/**
 * 业务数据缓存
 *
 * @author yanggy
 */
public class SeckillBusinessCache<T> extends SeckillCommonCache {
    /**
     * 数据
     */
    private T data;

    public SeckillBusinessCache<T> with(T data) {
        this.data = data;
        this.exist = true;
        return this;
    }

    public SeckillBusinessCache<T> withVersion(Long version) {
        this.version = version;
        return this;
    }

    public SeckillBusinessCache<T> retryLater() {
        this.retryLater = true;
        return this;
    }

    public SeckillBusinessCache<T> notExist() {
        this.exist = false;
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
