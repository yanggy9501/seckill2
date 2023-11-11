package com.freeing.seckill.goods.domain.service.impl;

import com.alibaba.fastjson.JSON;
import com.freeing.seckill.common.constants.SeckillConstants;
import com.freeing.seckill.common.enums.ErrorCode;
import com.freeing.seckill.common.event.publisher.EventPublisher;
import com.freeing.seckill.common.exception.SeckillException;
import com.freeing.seckill.common.model.enums.SeckillGoodsStatus;
import com.freeing.seckill.goods.domain.service.SeckillGoodsDomainService;
import com.freeing.seckill.goods.domain.event.SeckillGoodsEvent;
import com.freeing.seckill.goods.domain.model.entity.SeckillGoods;
import com.freeing.seckill.goods.domain.repository.SeckillGoodsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author yanggy
 */
@Service
public class SeckillGoodsDomainServiceImpl implements SeckillGoodsDomainService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillGoodsDomainServiceImpl.class);

    @Autowired
    private SeckillGoodsRepository seckillGoodsRepository;

    @Autowired
    private EventPublisher eventPublisher;

    @Value("${event.publish.type}")
    private String eventType;

    @Override
    public void saveSeckillGoods(SeckillGoods seckillGoods) {
        logger.info("goodsPublish|发布秒杀商品|{}", JSON.toJSONString(seckillGoods));
        if (Objects.isNull(seckillGoods) || !seckillGoods.validateParams()) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillGoods.setStatus(SeckillGoodsStatus.PUBLISHED.getCode());
        seckillGoodsRepository.saveSeckillGoods(seckillGoods);
        logger.info("goodsPublish|秒杀商品已经发布|{}", seckillGoods.getId());
        SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(seckillGoods.getId(),
            seckillGoods.getActivityId(),
            SeckillGoodsStatus.PUBLISHED.getCode(),
            this.getTopicEvent());
        eventPublisher.publish(seckillGoodsEvent);
        logger.info("goodsPublish|秒杀商品事件已经发布|{}", seckillGoods.getId());
    }

    @Override
    public SeckillGoods getSeckillGoodsId(Long id) {
        return seckillGoodsRepository.getSeckillGoodsId(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return null;
    }

    @Override
    public void updateStatus(Integer status, Long id) {

    }

    @Override
    public boolean updateAvailableStock(Integer count, Long id) {
        return false;
    }

    @Override
    public boolean updateDbAvailableStock(Integer count, Long id) {
        logger.info("goodsPublish|更新秒杀商品库存|id:{}, count:{}", id, count);
        if (Objects.isNull(count) || Objects.isNull(id) || count <= 0) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillGoodsRepository.updateAvailableStock(count, id) > 0;
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return null;
    }

    /**
     * 获取主题事件
     */
    private String getTopicEvent(){
        return SeckillConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ?
            SeckillConstants.TOPIC_EVENT_ROCKETMQ_GOODS : SeckillConstants.TOPIC_EVENT_COLA;
    }
}
