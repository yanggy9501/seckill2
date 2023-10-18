package com.freeing.seckill.user.config;

//@Component
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
public class RedisConfig {

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        //给key进行序列化
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        redisTemplate.setKeySerializer(stringRedisSerializer);
//        //给value进行序列化
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(DateTime.class,new JodaDateTimeJsonSerializer());
//        simpleModule.addDeserializer(DateTime.class,new JodaDateTimeJsonDeserializer());
//        //序列化的结果包含类的信息以及特殊属性类的信息
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        objectMapper.registerModule(simpleModule);
//
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
//        return redisTemplate;
//    }

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        //设置工厂链接
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        //设置自定义序列化方式
//        setSerializeConfig(redisTemplate, redisConnectionFactory);
//        return redisTemplate;
//    }
//
//    private void setSerializeConfig(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory redisConnectionFactory) {
//        //对字符串采取普通的序列化方式 适用于key 因为我们一般采取简单字符串作为key
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        //普通的string类型的key采用 普通序列化方式
//        redisTemplate.setKeySerializer(stringRedisSerializer);
//        //普通hash类型的key也使用 普通序列化方式
//        redisTemplate.setHashKeySerializer(stringRedisSerializer);
//        //解决查询缓存转换异常的问题  大家不能理解就直接用就可以了 这是springboot自带的jackson序列化类，但是会有一定问题
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        //普通的值采用jackson方式自动序列化
//        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
//        //hash类型的值也采用jackson方式序列化
//        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
//        //属性设置完成afterPropertiesSet就会被调用，可以对设置不成功的做一些默认处理
//        redisTemplate.afterPropertiesSet();
//    }
}
