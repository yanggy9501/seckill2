package com.freeing.seckill.common.model.dto;

import java.io.Serializable;

/**
 * @author yanggy
 */
public class SeckillUserDTO implements Serializable {
    private static final long serialVersionUID = 1576119726547415227L;

    private String userName;

    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
