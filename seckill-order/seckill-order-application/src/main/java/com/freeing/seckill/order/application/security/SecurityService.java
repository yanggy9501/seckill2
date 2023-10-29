package com.freeing.seckill.order.application.security;

/**
 * 模拟风控服务
 */
public interface SecurityService {

    /**
     * 对用户进行风控处理
     *
     * @param userId 用户ID
     * @return boolean
     */
    boolean securityPolicy(Long userId);
}
