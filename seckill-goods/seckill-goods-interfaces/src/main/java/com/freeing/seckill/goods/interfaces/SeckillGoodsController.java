package com.freeing.seckill.goods.interfaces;

import com.freeing.seckill.goods.application.model.command.SeckillGoodsCommand;
import com.freeing.seckill.goods.application.service.SeckillGoodsService;
import com.freeing.seckill.common.response.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品接口
 *
 * @author yanggy
 */
@RestController
@RequestMapping("/goods")
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @PostMapping("/saveSeckillGoods")
    public R saveSeckillGoods(SeckillGoodsCommand seckillGoodsCommand) {
        seckillGoodsService.saveSeckillGoods(seckillGoodsCommand);
        return R.success();
    }
}
