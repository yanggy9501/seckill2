package com.freeing.seckill.user.controller;

import com.freeing.seckill.common.model.dto.SeckillUserDTO;
import com.freeing.seckill.common.response.R;
import com.freeing.seckill.user.application.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yanggy
 */
@RestController
@RequestMapping("/user")
public class SeckillUserController {

    @Autowired
    private SeckillUserService seckillUserService;

    @PostMapping("/login")
    public R login(@RequestBody SeckillUserDTO seckillUserDTO) {
        String token = seckillUserService.login(seckillUserDTO.getUserName(), seckillUserDTO.getPassword());
        return R.success(token);
    }
}
