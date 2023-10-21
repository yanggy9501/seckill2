package com.freeing.seckill.activity.application.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.freeing.seckill.activity.application.command.SeckillActivityCommand;
import com.freeing.seckill.activity.domain.model.entity.SeckillActivity;
import com.freeing.seckill.common.cache.model.SeckillBusinessCache;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

/**
 * 秒杀活动构建类
 *
 * @author yanggy
 */
public class SeckillActivityBuilder {

    public static SeckillActivity toSeckillActivity(SeckillActivityCommand seckillActivityCommand) {
        if (Objects.isNull(seckillActivityCommand)) {
            return null;
        }
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtils.copyProperties(seckillActivityCommand, seckillActivity);
        return seckillActivity;
    }

    public static <T> SeckillBusinessCache<T> getSeckillBusinessCache(Object object, Class<T> clazz){
        if (object == null){
            return null;
        }
        return JSON.parseObject(object.toString(), new TypeReference<SeckillBusinessCache<T>>(clazz){});
    }
}
