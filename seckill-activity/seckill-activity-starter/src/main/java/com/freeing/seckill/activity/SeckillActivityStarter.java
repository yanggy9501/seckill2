package com.freeing.seckill.activity;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yanggy
 */
@SpringBootApplication
@EnableDubbo
public class SeckillActivityStarter {
    public static void main(String[] args) {
        SpringApplication.run(SeckillActivityStarter.class, args);
    }
}
