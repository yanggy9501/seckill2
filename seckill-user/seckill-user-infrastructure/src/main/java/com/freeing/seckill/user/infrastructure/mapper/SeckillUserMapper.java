package com.freeing.seckill.user.infrastructure.mapper;

import com.freeing.seckill.user.domain.entity.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SeckillUserMapper {
    /**
     * 根据用户名获取用户信息
     */
    SeckillUser getSeckillUserByUserName(@Param("userName") String userName);
}
