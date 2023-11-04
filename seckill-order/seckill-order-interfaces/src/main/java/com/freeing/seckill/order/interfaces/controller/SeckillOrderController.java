package com.freeing.seckill.order.interfaces.controller;

import com.freeing.seckill.common.model.dto.SeckillOrderSubmitDTO;
import com.freeing.seckill.common.response.R;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.application.service.SeckillSubmitOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀订单 Controller
 *
 * @author yanggy
 */
@RestController
@RequestMapping("/order")
public class SeckillOrderController {

    @Autowired
    private SeckillSubmitOrderService seckillSubmitOrderService;

    /**
     * 保存秒杀订单
     */
    @PostMapping(value = "/saveSeckillOrder")
    public R saveSeckillOrder(@RequestAttribute Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillOrderSubmitDTO seckillOrderSubmitDTO =
            seckillSubmitOrderService.saveSeckillOrder(userId, seckillOrderCommand);
        return R.success(seckillOrderSubmitDTO);
    }
}
