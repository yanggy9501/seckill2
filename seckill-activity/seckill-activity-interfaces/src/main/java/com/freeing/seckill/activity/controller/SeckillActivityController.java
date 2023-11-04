package com.freeing.seckill.activity.controller;

import com.freeing.seckill.activity.application.command.SeckillActivityCommand;
import com.freeing.seckill.activity.application.service.SeckillActivityService;
import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;
import com.freeing.seckill.common.model.dto.SeckillActivityDTO;
import com.freeing.seckill.common.response.R;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * @author yanggy
 */
@RestController
@RequestMapping("/activity")
public class SeckillActivityController {

    @Autowired
    private SeckillActivityService seckillActivityService;

    @PostMapping("/saveSeckillActivity")
    public R saveSeckillActivity(SeckillActivityCommand seckillActivityCommand) {
        seckillActivityService.saveSeckillActivity(seckillActivityCommand);
        return R.success();
    }

    // TODO 完善
    @GetMapping("/getSeckillActivityList")
    public R getSeckillActivityList(@RequestParam(value = "status", required = false) Integer status) {
        List<SeckillActivity> seckillActivityList = seckillActivityService.getSeckillActivityList(status);
        return R.success(seckillActivityList);
    }

    // TODO 完善
    @RequestMapping(value = "/getSeckillActivityListBetweenStartTimeAndEndTime", method = {RequestMethod.GET, RequestMethod.POST})
    public R getSeckillActivityListBetweenStartTimeAndEndTime(
        @RequestParam(value = "currentTime", required = false) String currentTime,
        @RequestParam(value = "status", required = false) Integer status) throws ParseException {
        List<SeckillActivity> seckillActivityList = seckillActivityService
            .getSeckillActivityListBetweenStartTimeAndEndTime(DateUtils.parseDate(currentTime, "yyyy-MM-dd HH:mm:ss"), status);
        return R.success(seckillActivityList);
    }

    /**
     * 获取秒杀活动列表
     */
    @GetMapping(value = "/seckillActivityList")
    public R getSeckillActivityList(
        @RequestParam(value = "status", required = false) Integer status,
        @RequestParam(value = "version", required = false) Long version) {
        List<SeckillActivityDTO> seckillActivityList = seckillActivityService.getSeckillActivityList(status, version);
        return R.success(seckillActivityList);
    }

    /**
     * 获取秒杀活动列表
     */
    @RequestMapping(value = "/seckillActivityListBetweenStartTimeAndEndTime", method = {RequestMethod.GET, RequestMethod.POST})
    public R getSeckillActivityListBetweenStartTimeAndEndTime(
        @RequestParam(value = "currentTime", required = false) String currentTime,
        @RequestParam(value = "status", required = false) Integer status,
        @RequestParam(value = "version", required = false) Long version) throws ParseException {
        List<SeckillActivityDTO> seckillActivityList = seckillActivityService
            .getSeckillActivityListBetweenStartTimeAndEndTime(DateUtils.parseDate(currentTime, "yyyy-MM-dd HH:mm:ss"), status, version);
        return R.success(seckillActivityList);
    }

    /**
     * 获取id获取秒杀活动详情
     */
    @RequestMapping(value = "/seckillActivity", method = {RequestMethod.GET, RequestMethod.POST})
    public R getSeckillActivityById(@RequestParam(value = "id", required = false) Long id,
        @RequestParam(value = "version", required = false) Long version) {
        return R.success(seckillActivityService.getSeckillActivity(id, version));
    }

    /**
     * 获取id获取秒杀活动详情
     */
    @RequestMapping(value = "/getSeckillActivityById", method = {RequestMethod.GET, RequestMethod.POST})
    public R getSeckillActivityById(@RequestParam(value = "id", required = false) Long id) {
        return R.success(seckillActivityService.getSeckillActivityById(id));
    }

    /**
     * 更新活动的状态
     */
    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET, RequestMethod.POST})
    public R updateStatus(@RequestParam(value = "status", required = false) Integer status,
        @RequestParam(value = "id", required = false) Long id) {
        seckillActivityService.updateStatus(status, id);
        return R.success();
    }
}
