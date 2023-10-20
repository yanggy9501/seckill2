package com.freeing.seckill.common.lock.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Redisson配置
 *
 * @author yanggy
 */
@Configuration
@ConditionalOnProperty(name = "distributed.lock.type", havingValue = "redisson")
public class RedissonConfig {
    @Value("${spring.redis.address}")
    private String redisAddress;

    /**
     * 单机模式
     *
     * @return
     */
    @Bean(name = "redissonClient")
    @ConditionalOnProperty(name = "redis.arrange.type", havingValue = "single")
    public RedissonClient singleRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisAddress).setDatabase(0);
        return Redisson.create(config);
    }

    /**
     * 集群模式
     *
     * @return
     */
    @Bean(name = "redissonClient")
    @ConditionalOnProperty(name = "redis.arrange.type", havingValue = "cluster")
    public RedissonClient clusterRedissonClient(){
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers();
        clusterServersConfig.setNodeAddresses(Arrays.asList(redisAddress));
        return Redisson.create(config);
    }
}
