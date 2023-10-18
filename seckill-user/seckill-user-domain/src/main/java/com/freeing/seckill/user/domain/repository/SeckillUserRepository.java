package com.freeing.seckill.user.domain.repository;

import com.freeing.seckill.user.domain.entity.SeckillUser;

/**
 * @author yanggy
 */
public interface SeckillUserRepository {
    /**
     * 根据用户名获取用户信息
     */
    SeckillUser getSeckillUserByUserName(String userName);
}
