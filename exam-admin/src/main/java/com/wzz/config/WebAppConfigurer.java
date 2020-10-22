package com.wzz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Date 2020/10/22 10:57
 * @created by wzz
 */

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Bean
    public AdminInterceptor getAdminInterceptor() {
        return  new AdminInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可添加多个
        //拦截未登录进入超级管理员的界面
        registry.addInterceptor(getAdminInterceptor()).addPathPatterns("/admin/**");
    }

}
