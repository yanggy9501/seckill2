package com.freeing.seckill.order.application.service.impl;

import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.model.dto.SeckillOrderSubmitDTO;
import com.freeing.seckill.order.application.model.command.SeckillOrderCommand;
import com.freeing.seckill.order.application.service.SeckillSubmitOrderService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 同步方式提交订单
 *
 * @author yanggy
 */
@Service
@ConditionalOnProperty(name = "submit.order.type", havingValue = "sync", matchIfMissing = true)
public class SeckillSyncSubmitOrderServiceImpl extends SeckillBaseSubmitOrderServiceImpl implements SeckillSubmitOrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        this.checkSeckillOrder(userId, seckillOrderCommand);
        Long txNo = this.seckillPlaceOrderService.placeOrder(userId, seckillOrderCommand);
        return new SeckillOrderSubmitDTO(String.valueOf(txNo), seckillOrderCommand.getGoodsId(), SeckillConstants.TYPE_ORDER);
    }
}
