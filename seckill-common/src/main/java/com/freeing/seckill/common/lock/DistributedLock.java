package com.freeing.seckill.common.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 */
public interface DistributedLock {
    /**
     * 尝试获取指定定租赁时间的锁
     *
     * @param waitTime 获取锁的最大等候时间
     * @param leaseTime 占用锁的时间
     * @param unit 时间单位
     * @return
     * @throws InterruptedException
     */
    boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    boolean tryLock() throws InterruptedException;

    void lock(long leaseTime, TimeUnit unit);

    void unlock();

    boolean isLocked();

    /**
     * 是否由 threadId 线程持有
     *
     * @param threadId
     * @return
     */
    boolean isHeldByThread(long threadId);

    /**
     * 是否由当前线程持有
     *
     * @return
     */
    boolean isHeldByCurrentThread();
}
