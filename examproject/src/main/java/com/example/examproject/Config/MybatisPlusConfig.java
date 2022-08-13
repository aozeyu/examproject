package com.example.examproject.Config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @program: examproject
 * @description: mybatisplus配置
 * @packagename: com.example.examproject.Config
 * @author: 姚泽宇
 * @date: 2022-08-13 13:48
 **/
@MapperScan("com.example.examproject.Mapper")
@Configuration
public class MybatisPlusConfig {
    //分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * SQL执行效率插件
     */
    @Bean
    @Profile({"dev","test"})// 设置 dev test 环境开启
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        performanceInterceptor.setMaxTime(1000);//ms 设置sql执行的最大时间,如果超过了则不执行
        performanceInterceptor.setFormat(true);
        return performanceInterceptor;
    }
}
