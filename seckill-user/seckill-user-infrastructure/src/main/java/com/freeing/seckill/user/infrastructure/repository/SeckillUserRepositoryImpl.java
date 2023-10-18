package com.freeing.seckill.user.infrastructure.repository;

import com.freeing.seckill.user.domain.entity.SeckillUser;
import com.freeing.seckill.user.domain.repository.SeckillUserRepository;
import com.freeing.seckill.user.infrastructure.mapper.SeckillUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author yanggy
 */
@Repository
public class SeckillUserRepositoryImpl implements SeckillUserRepository {

    @Autowired
    private SeckillUserMapper seckillUserMapper;

    @Override
    public SeckillUser getSeckillUserByUserName(String userName) {
        return seckillUserMapper.getSeckillUserByUserName(userName);
    }
}
