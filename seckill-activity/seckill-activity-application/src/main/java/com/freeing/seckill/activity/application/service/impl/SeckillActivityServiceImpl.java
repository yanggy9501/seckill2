package com.freeing.seckill.activity.application.service.impl;

import com.freeing.seckill.activity.application.builder.SeckillActivityBuilder;
import com.freeing.seckill.activity.application.cache.SeckillActivityCacheService;
import com.freeing.seckill.activity.application.cache.SeckillActivityListCacheService;
import com.freeing.seckill.activity.application.command.SeckillActivityCommand;
import com.freeing.seckill.activity.application.service.SeckillActivityService;
import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;
import com.freeing.seckill.activity.domain.service.SeckillActivityDomainService;
import com.freeing.seckill.common.cache.model.SeckillBusinessCache;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.dto.SeckillActivityDTO;
import com.freeing.seckill.common.model.enums.SeckillActivityStatus;
import com.freeing.seckill.common.util.id.SnowFlakeFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yanggy
 */
@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {

    @Autowired
    private SeckillActivityDomainService seckillActivityDomainService;

    @Autowired
    private SeckillActivityListCacheService seckillActivityListCacheService;

    @Autowired
    private SeckillActivityCacheService seckillActivityCacheService;


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
        return seckillActivityDomainService.getSeckillActivityList(status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return null;
    }

    @Override
    public List<SeckillActivityDTO> getSeckillActivityList(Integer status, Long version) {
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache =
            seckillActivityListCacheService.getCachedActivities(status, version);
        // 稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillActivitiyListCache.isRetryLater()) {
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        if (!seckillActivitiyListCache.isExist()) {
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }

        List<SeckillActivity> seckillActivityList = seckillActivitiyListCache.getData();
        List<SeckillActivityDTO> seckillActivityDTOList = seckillActivityList.stream().map(activity -> {
            SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
            BeanUtils.copyProperties(activity, seckillActivityDTO);
            seckillActivityDTO.setVersion(seckillActivitiyListCache.getVersion());
            return seckillActivityDTO;
        }).collect(Collectors.toList());

        return seckillActivityDTOList;
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
        if (id == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillActivity> seckillActivityCache =
            seckillActivityCacheService.getCachedSeckillActivity(id, version);

        // 稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillActivityCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        // 缓存中的活动数据不存在
        if (!seckillActivityCache.isExist()){
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillActivityDTO seckillActivityDTO = SeckillActivityBuilder.toSeckillActivityDTO(seckillActivityCache.getData());
        seckillActivityDTO.setVersion(seckillActivityCache.getVersion());
        return seckillActivityDTO;
    }

    @Override
    public void updateStatus(Integer status, Long id) {

    }
}
