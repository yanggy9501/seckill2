package com.freeing.seckill.user.domain.entity;

import java.io.Serializable;

/**
 * @author yanggy
 */
public class SeckillUser implements Serializable {
    private static final long serialVersionUID = -3004624289691589697L;

    private Long id;

    private String userName;

    private String password;

    /**
     * 1：正常；2：冻结
     */
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
