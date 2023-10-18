package com.freeing.seckill.user.application.service.impl;

import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.shiro.utils.CommonsUtils;
import com.freeing.seckill.common.shiro.utils.JwtUtils;
import com.freeing.seckill.user.application.service.SeckillUserService;
import com.freeing.seckill.user.domain.entity.SeckillUser;
import com.freeing.seckill.user.domain.repository.SeckillUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yanggy
 */
@Service
public class SeckillUserServiceImpl implements SeckillUserService {

    @Autowired
    private SeckillUserRepository seckillUserRepository;

//    @Autowired
//    private DistributedCacheService distributedCacheService;

    @Override
    public String login(String userName, String password) {
        if (StringUtils.isEmpty(userName)){
            throw new SeckillException(ErrorCode.USERNAME_IS_NULL);
        }
        if (StringUtils.isEmpty(password)){
            throw new SeckillException(ErrorCode.PASSWORD_IS_NULL);
        }
        SeckillUser seckillUser = seckillUserRepository.getSeckillUserByUserName(userName);
        if (seckillUser == null){
            throw new SeckillException(ErrorCode.USERNAME_IS_ERROR);
        }
        String paramsPassword = CommonsUtils.encryptPassword(password, userName);
        if (!paramsPassword.equals(seckillUser.getPassword())){
            throw new SeckillException(ErrorCode.PASSWORD_IS_ERROR);
        }
        String token = JwtUtils.sign(seckillUser.getId());
        String key = SeckillConstants.getKey(SeckillConstants.USER_KEY_PREFIX, String.valueOf(seckillUser.getId()));

        return token;
    }
}
