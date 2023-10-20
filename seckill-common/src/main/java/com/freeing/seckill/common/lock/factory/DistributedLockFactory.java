package com.freeing.seckill.common.lock.factory;

import com.freeing.seckill.common.lock.DistributedLock;

/**
 * 分布式锁工厂
 */
public interface DistributedLockFactory {
    /**
     * 根据key获取分布式锁
     */
    DistributedLock getDistributedLock(String key);
}
