package com.freeing.seckill.common.exception.handler;

import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.response.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局统一异常处理器
 *
 * @author yanggy
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 全局异常处理，统一返回状态码
     */
    @ExceptionHandler(SeckillException.class)
    public R handleSeckillException(SeckillException e) {
        logger.error("服务器抛出了异常", e);
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 全局异常处理，统一返回状态码
     */
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        logger.error("服务器抛出了异常", e);
        return R.error(ErrorCode.SERVER_EXCEPTION.getCode(), e.getMessage());
    }
}
