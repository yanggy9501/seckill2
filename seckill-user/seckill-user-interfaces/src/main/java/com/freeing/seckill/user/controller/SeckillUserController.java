package com.freeing.seckill.user.controller;

import com.freeing.seckill.common.model.dto.SeckillUserDTO;
import com.freeing.seckill.common.response.R;
import com.freeing.seckill.user.application.service.SeckillUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yanggy
 */
@Api(tags = "用户 Controller")
@RestController
@RequestMapping("/user")
public class SeckillUserController {

    @Autowired
    private SeckillUserService seckillUserService;

    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public R login(@RequestBody SeckillUserDTO seckillUserDTO) {
        String token = seckillUserService.login(seckillUserDTO.getUserName(), seckillUserDTO.getPassword());
        return R.success(token);
    }
}
