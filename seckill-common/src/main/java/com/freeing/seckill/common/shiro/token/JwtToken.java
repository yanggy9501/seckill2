package com.freeing.seckill.common.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 *  自定义的JwtToken类
 */
public class JwtToken implements AuthenticationToken {

	private static final long serialVersionUID = 4758816034257581191L;
	private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
