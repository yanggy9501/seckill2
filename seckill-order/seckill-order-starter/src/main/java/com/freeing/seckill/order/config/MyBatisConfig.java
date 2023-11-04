package com.freeing.seckill.order.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  MyBatis配置类
 */
@Configuration
public class MyBatisConfig {

    @Value("${mybatis.scanpackages}")
    private String scanPackages;

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DruidDataSource dataSource){
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setTypeAliasesPackage(scanPackages);
        return sqlSessionFactory;
    }
}
