package com.freeing.seckill.activity.controller;

import com.freeing.seckill.activity.application.command.SeckillActivityCommand;
import com.freeing.seckill.activity.application.service.SeckillActivityService;
import com.freeing.seckill.common.response.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yanggy
 */
@Api( tags = "秒杀活动 Controller")
@RestController
@RequestMapping("/activity")
public class SeckillActivityController {

    @Autowired
    private SeckillActivityService seckillActivityService;

    @ApiOperation(value = "保存秒杀活动")
    @PostMapping("/saveSeckillActivity")
    public R saveSeckillActivity(SeckillActivityCommand seckillActivityCommand) {
        seckillActivityService.saveSeckillActivity(seckillActivityCommand);
        return R.success();
    }
}
