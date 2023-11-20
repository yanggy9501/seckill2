package com.freeing.seckill.order.application.service.impl;

import com.freeing.seckill.order.application.service.OrderTaskGenerateService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 生成订单下单请求标识实现类
 *
 * @author yanggy
 */
@Service
public class OrderTaskGenerateServiceImpl implements OrderTaskGenerateService {
    @Override
    public String generatePlaceOrderTaskId(Long userId, Long goodsId) {
        String toEncrypt = userId + "_" + goodsId;
        return DigestUtils.md5DigestAsHex(toEncrypt.getBytes());
    }
}
