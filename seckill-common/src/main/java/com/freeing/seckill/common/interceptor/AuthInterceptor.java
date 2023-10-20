package com.freeing.seckill.common.interceptor;

import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.shiro.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author yanggy
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String USER_ID = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (loginAlready(request)) {
            return true;
        }

        String token = request.getHeader(SeckillConstants.TOKEN_HEADER_NAME);
        if (StringUtils.isEmpty(token)) {
            throw new SeckillException(ErrorCode.USER_NOT_LOGIN);
        }
        Long userId = JwtUtils.getUserId(token);
        request.setAttribute(USER_ID, userId);
        return true;
    }

    private boolean loginAlready(HttpServletRequest request) {
        Object userIdObj = request.getAttribute(USER_ID);
        if (Objects.isNull(userIdObj)) {
            return true;
        }
        return false;
    }
}
