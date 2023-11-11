package com.freeing.seckill.goods.application.duboo;

import com.freeing.seckill.goods.application.service.SeckillGoodsService;
import com.freeing.seckill.common.model.dto.SeckillGoodsDTO;
import com.freeing.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品 Dubbo 服务实现类
 *
 * @author yanggy
 */
@Service
@DubboService(version = "1.0.0")
public class SeckillGoodsDubboServiceImpl implements SeckillGoodsDubboService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Override
    public SeckillGoodsDTO getSeckillGoods(Long goodsId, Long version) {
        return seckillGoodsService.getSeckillGoods(goodsId, version);
    }

    @Override
    public Integer getAvailableStockById(Long goodsId) {
        return null;
    }

    @Override
    public boolean updateDbAvailableStock(Integer quantity, Long goodsId) {
        return seckillGoodsService.updateDbAvailableStock(quantity, goodsId);
    }
}
