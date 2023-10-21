package com.freeing.seckill.activity.application.service.impl;

import com.freeing.seckill.activity.application.builder.SeckillActivityBuilder;
import com.freeing.seckill.activity.application.command.SeckillActivityCommand;
import com.freeing.seckill.activity.application.service.SeckillActivityService;
import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;
import com.freeing.seckill.activity.domain.service.SeckillActivityDomainService;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.dto.SeckillActivityDTO;
import com.freeing.seckill.common.model.enums.SeckillActivityStatus;
import com.freeing.seckill.common.util.id.SnowFlakeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author yanggy
 */
@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {

    @Autowired
    private SeckillActivityDomainService seckillActivityDomainService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSeckillActivity(SeckillActivityCommand seckillActivityCommand) {
        if (Objects.isNull(seckillActivityCommand)) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillActivity seckillActivity = SeckillActivityBuilder.toSeckillActivity(seckillActivityCommand);
        seckillActivity.setId(SnowFlakeFactory.getSnowFlake().nextId());
        seckillActivity.setStatus(SeckillActivityStatus.PUBLISHED.getCode());
        seckillActivityDomainService.saveSeckillActivity(seckillActivity);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return null;
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return null;
    }

    @Override
    public List<SeckillActivityDTO> getSeckillActivityList(Integer status, Long version) {
        return null;
    }

    @Override
    public List<SeckillActivityDTO> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status, Long version) {
        return null;
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return null;
    }

    @Override
    public SeckillActivityDTO getSeckillActivity(Long id, Long version) {
        return null;
    }

    @Override
    public void updateStatus(Integer status, Long id) {

    }
}
