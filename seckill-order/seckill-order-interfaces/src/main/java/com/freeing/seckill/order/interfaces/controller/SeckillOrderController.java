package com.freeing.seckill.order.interfaces.controller;

import com.freeing.seckill.common.model.dto.SeckillOrderSubmitDTO;
import com.freeing.seckill.common.response.R;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.application.service.SeckillSubmitOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀订单 Controller
 *
 * @author yanggy
 */
@RestController
public class SeckillOrderController {

    @Autowired
    private SeckillSubmitOrderService seckillSubmitOrderService;

    /**
     * 保存秒杀订单
     */
    @RequestMapping(value = "/saveSeckillOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public R saveSeckillOrder(@RequestAttribute Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillOrderSubmitDTO seckillOrderSubmitDTO = seckillSubmitOrderService.saveSeckillOrder(userId, seckillOrderCommand);
        return R.success(seckillOrderSubmitDTO);
    }
}
