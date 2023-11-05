package com.freeing.seckill.order.application.service.impl;

import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;
import com.freeing.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.application.place.SeckillPlaceOrderService;
import com.freeing.seckill.order.application.security.SecurityService;
import com.freeing.seckill.order.application.service.SeckillSubmitOrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 提交订单基础实现类
 *
 * @author yanggy
 */
public abstract class SeckillBaseSubmitOrderServiceImpl implements SeckillSubmitOrderService {

    @Autowired
    private SecurityService securityService;

    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;

    @Autowired
    protected SeckillPlaceOrderService seckillPlaceOrderService;

    @Override
    public void checkSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        if (userId == null || seckillOrderCommand == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        // 模拟风控
        if (!securityService.securityPolicy(userId)){
            throw new SeckillException(ErrorCode.USER_INVALID);
        }
        // 获取商品信息
        SeckillGoodsDTO seckillGoods  =
            seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品信息
        seckillPlaceOrderService.checkSeckillGoods(seckillOrderCommand, seckillGoods);
    }
}
