//package com.example.examproject.Config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * @program: examproject
// * @description: 222
// * @packagename: com.example.examproject.Config
// * @author: 姚泽宇
// * @date: 2022-08-13 21:50
// **/
//@Configuration
//public class WebAppConfigurer implements WebMvcConfigurer {
//
//    @Bean
//    public AdminInterceptor getAdminInterceptor() {
//        return  new AdminInterceptor();
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 可添加多个
//        //拦截未登录进入超级管理员的界面
//        registry.addInterceptor((HandlerInterceptor) getAdminInterceptor()).addPathPatterns("/admin/**");
//    }
//
//}
