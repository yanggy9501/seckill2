package com.freeing.seckill.order.application.security;

import org.springframework.stereotype.Service;

/**
 * 模拟风控
 *
 * @author yanggy
 */
@Service
public class DefaultSecurityServiceImpl implements SecurityService {
    @Override
    public boolean securityPolicy(Long userId) {
        return true;
    }
}
