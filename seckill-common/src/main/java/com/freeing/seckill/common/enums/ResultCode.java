package com.freeing.seckill.common.enums;

/**
 * 返回状态码
 * 原则：2 + 3 位状态码，前 2 位代表模块，后 3 位代码具体“状态”
 *
 * @author yanggy
 */
public enum ResultCode {
    ERROR(1000, "未知异常"),
    SUCCESS(1001, "成功"),
    FAILURE(2001, "失败"),
    ;

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态信息
     */
    private final String msg;

    ResultCode(Integer code, String msg ) {
        this.code = code;
        this.msg = msg;
    }

    public static ResultCode get(Integer code) {
        for (ResultCode value : ResultCode.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Illegal R code '" + code + "'");
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
