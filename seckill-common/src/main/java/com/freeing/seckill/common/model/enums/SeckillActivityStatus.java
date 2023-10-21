package com.freeing.seckill.common.model.enums;

public enum SeckillActivityStatus {
    PUBLISHED(0),
    ONLINE(1),
    OFFLINE(-1);

    private final Integer code;

    SeckillActivityStatus(Integer code) {
        this.code = code;
    }

    public static boolean isOffline(Integer status) {
        return OFFLINE.getCode().equals(status);
    }

    public static boolean isOnline(Integer status) {
        return ONLINE.getCode().equals(status);
    }

    public Integer getCode() {
        return code;
    }
}
