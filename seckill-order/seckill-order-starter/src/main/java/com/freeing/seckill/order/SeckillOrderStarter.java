package com.freeing.seckill.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 订单服务启动类
 *
 * @author yanggy
 */
@EnableDubbo
@SpringBootApplication
public class SeckillOrderStarter {
    public static void main(String[] args) {
        SpringApplication.run(SeckillOrderStarter.class, args);
    }
}
