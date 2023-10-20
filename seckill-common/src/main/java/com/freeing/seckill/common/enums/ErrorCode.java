package com.freeing.seckill.common.enums;

/**
 * 异常状态状态码
 *
 * @author yanggy
 */
public enum ErrorCode {
    PARAMS_INVALID(2002, "参数错误"),
    USERNAME_IS_NULL(2003, "用户名不能为空"),
    PASSWORD_IS_NULL(2004, "密码不能为空"),
    USERNAME_IS_ERROR(2005, "用户名错误"),
    PASSWORD_IS_ERROR(2006, "密码错误"),
    SERVER_EXCEPTION(2007, "服务器异常"),
    STOCK_LT_ZERO(2008, "库存不足"),
    GOODS_NOT_EXISTS(2009, "当前商品不存在"),
    ACTIVITY_NOT_EXISTS(2010, "当前活动不存在"),
    BEYOND_LIMIT_NUM(2011, "下单数量不能超过限购数量"),
    USER_NOT_LOGIN(2012, "用户未登录"),
    TOKEN_EXPIRE(2013, "Token失效"),
    GOODS_OFFLINE(2014, "商品已下线"),
    DATA_PARSE_FAILED(2015, "数据解析失败"),
    RETRY_LATER(2016, "稍后再试"),
    USER_INVALID(2017, "当前账户异常，不能参与秒杀"),
    GOODS_PUBLISH(2018, "商品未上线"),
    ORDER_FAILED(2019, "下单失败"),
    BEYOND_TIME(2020, "超出活动时间"),
    GOODS_FINISH(2021, "商品已售罄"),
    REDUNDANT_SUBMIT(2022, "请勿重复下单"),
    ORDER_TOKENS_NOT_AVAILABLE(2023, "暂无可用库存"),
    ORDER_TASK_ID_INVALID(2024, "下单任务编号错误"),
    STOCK_IS_NULL(2025, "商品库存不存在");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态信息
     */
    private final String mesaage;

    ErrorCode(Integer code, String mesaage ) {
        this.code = code;
        this.mesaage = mesaage;
    }

    public static ErrorCode get(Integer code) {
        for (ErrorCode value : ErrorCode.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Illegal R code '" + code + "'");
    }

    public Integer getCode() {
        return code;
    }

    public String getMesaage() {
        return mesaage;
    }

}
