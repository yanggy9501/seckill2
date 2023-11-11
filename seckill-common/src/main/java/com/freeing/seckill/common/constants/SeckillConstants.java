package com.freeing.seckill.common.constants;

/**
 * @author yanggy
 */
public class SeckillConstants {
    /**
     * LUA脚本商品库存不存在
     */
    public static final int LUA_RESULT_GOODS_STOCK_NOT_EXISTS = -1;

    /**
     * LUA脚本要扣减的商品数量小于等于0
     */
    public static final int LUA_RESULT_GOODS_STOCK_PARAMS_LT_ZERO = -2;

    /**
     * LUA脚本库存不足
     */
    public static final int LUA_RESULT_GOODS_STOCK_LT_ZERO = -3;

    /**
     * 已经执行过恢复缓存库存的操作
     */
    public static final Long CHECK_RECOVER_STOCK_HAS_EXECUTE = 0L;

    /**
     * 未执行过恢复缓存库存的操作
     */
    public static final Long CHECK_RECOVER_STOCK_NOT_EXECUTE = 1L;

    /**
     * 事务日志7天过期
     */
    public static final long TX_LOG_EXPIRE_DAY = 7;

    /**
     * 事务日志过期秒数
     */
    public static final long TX_LOG_EXPIRE_SECONDS = 7 * 24 * 3600;

    /**
     * 商品key前缀
     */
    public static final String GOODS_ITEM_KEY_PREFIX = "item:";

    /**
     * 商品事务列表
     */
    public static final String GOODS_TX_KEY = "item_tx:";

    /**
     * 订单事务列表
     */
    public static final String ORDER_TX_KEY = "order_tx:";

    /**
     * 事务消息主题
     */
    public static final String TOPIC_TX_MSG = "topic_tx_msg";

    /**
     * 异常消息主题
     */
    public static final String TOPIC_ERROR_MSG = "topic_error_msg";

    /**
     * 数据库方式
     */
    public static final String PLACE_ORDER_TYPE_DB = "db";

    /**
     * 分布式锁方法
     */
    public static final String PLACE_ORDER_TYPE_LOCK = "lock";

    /**
     * lua脚本方式
     */
    public static final String PLACE_ORDER_TYPE_LUA = "lua";

    /**
     * 事务消息的key
     */
    public static final String TX_MSG_KEY = "txMessage";

    /**
     * 错误消息的key
     */
    public static final String ERROR_MSG_KEY = "errorMessage";

    /**
     * 事件消息Key
     */
    public static final String EVENT_MSG_KEY = "eventMessage";

    /**
     * 活动事件消息topic
     */
    public static final String TOPIC_EVENT_ROCKETMQ_ACTIVITY = "topic_event_rocketmq_activity";

    /**
     * 商品事件消息topic
     */
    public static final String TOPIC_EVENT_ROCKETMQ_GOODS = "topic_event_rocketmq_goods";

    /**
     * 订单事件消息topic
     */
    public static final String TOPIC_EVENT_ROCKETMQ_ORDER = "topic_event_rocketmq_order";

    /**
     * 订单消费分组
     */
    public static final String EVENT_ORDER_CONSUMER_GROUP = "event_order_consumer_group";

    /**
     * 商品消费分组
     */
    public static final String EVENT_GOODS_CONSUMER_GROUP = "event_goods_consumer_group";

    /**
     * 活动消费分组
     */
    public static final String EVENT_ACTIVITY_CONSUMER_GROUP = "event_activity_consumer_group";


    /**
     * Cola订阅事件
     */
    public static final String TOPIC_EVENT_COLA = "topic_event_cola";

    /**
     * cola事件类型
     */
    public static final String EVENT_PUBLISH_TYPE_COLA = "cola";

    /**
     * RocketMQ事件类型
     */
    public static final String EVENT_PUBLISH_TYPE_ROCKETMQ = "rocketmq";

    /**
     * 订单事务分组
     */
    public static final String TX_ORDER_PRODUCER_GROUP = "tx_order_producer_group";

    /**
     * 订单消费分组
     */
    public static final String TX_ORDER_CONSUMER_GROUP = "tx_order_consumer_group";

    /**
     * 商品事务分组
     */
    public static final String TX_GOODS_PRODUCER_GROUP = "tx_goods_producer_group";

    /**
     * 商品消费分组
     */
    public static final String TX_GOODS_CONSUMER_GROUP = "tx_goods_condumer_group";


    /**
     * 订单Key前缀
     */
    public static final String ORDER_KEY_PREFIX = "order:";

    /**
     * Lua脚本后缀
     */
    public static final String LUA_SUFFIX = "_lua";

    /**
     * 订单锁
     */
    public static final String ORDER_LOCK_KEY_PREFIX = "order:lock:";

    /**
     * 商品库存的Key
     */
    public static final String GOODS_ITEM_STOCK_KEY_PREFIX = "item:stock:";

    /**
     * 商品限购数量Key
     */
    public static final String GOODS_ITEM_LIMIT_KEY_PREFIX = "item:limit:";

    /**
     * 商品上架标识
     */
    public static final String GOODS_ITEM_ONLINE_KEY_PREFIX = "item:onffline:";

    /**
     * 用户缓存前缀
     */
    public static final String USER_KEY_PREFIX = "user:";

    /**
     * 获取Key
     */
    public static String getKey(String prefix, String key){
        return prefix.concat(key);
    }

    /**
     * token的载荷中盛放的信息 只盛放一个userName 其余什么也不再盛放
     */
    public static final String TOKEN_CLAIM = "userId";

    /**
     * jwtToken过期时间 默认为7天
     */
    public static final Long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    /**
     * token请求头名称
     */
    public static final String TOKEN_HEADER_NAME = "access-token";

    /**
     * JWT的密钥
     */
    public static final String JWT_SECRET = "a814edb0e7c1ba4c";


    /*****************缓存相关的配置****************/
    public static final Long FIVE_MINUTES = 5 * 60L;
    public static final Long FIVE_SECONDS = 5L;
    public static final Long HOURS_24 = 3600 * 24L;

    public static final String SECKILL_ACTIVITY_CACHE_KEY = "SECKILL_ACTIVITY_CACHE_KEY";
    public static final String SECKILL_ACTIVITIES_CACHE_KEY = "SECKILL_ACTIVITIES_CACHE_KEY";

    public static final String SECKILL_GOODS_CACHE_KEY = "SECKILL_GOODS_CACHE_KEY";
    public static final String SECKILL_GOODSES_CACHE_KEY = "SECKILL_GOODSES_CACHE_KEY";

}
