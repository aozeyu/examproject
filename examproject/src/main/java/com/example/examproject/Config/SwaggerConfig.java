package com.example.examproject.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @program: examproject
 * @description: 接口文档配置
 * @packagename: com.example.examproject.Config
 * @author: 姚泽宇
 * @date: 2022-08-13 13:50
 **/
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).pathMapping("/").select().paths(PathSelectors.regex("./*")).build();
    }


    private ApiInfo apiInfo(){
        return new ApiInfoBuilder().title("在线考试系统文档")
                .contact(new Contact("github地址","https://gihub.com/aozeyu/examproject", "1206414629@qq.com"))
                .description("在线考试系统集成swaggerUi生成的接口文档")
                .version("1.0")
                .build();
    }
}
