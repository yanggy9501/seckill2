package com.freeing.seckill.activity.application.dubbo;

import com.freeing.seckill.activity.application.service.SeckillActivityService;
import com.freeing.seckill.common.model.dto.SeckillActivityDTO;
import com.freeing.seckill.dubbo.interfaces.activity.SeckillActivityDubboService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Dubbo 服务
 * @author yanggy
 */
@Component
@DubboService(version = "1.0.0")
public class SeckillActivityDubboServiceImpl implements SeckillActivityDubboService {

    @Autowired
    private SeckillActivityService seckillActivityService;

    @Override
    public SeckillActivityDTO getSeckillActivity(Long id, Long version) {
        return seckillActivityService.getSeckillActivity(id, version);
    }
}
