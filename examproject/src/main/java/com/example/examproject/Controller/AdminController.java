package com.example.examproject.Controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: examproject
 * @description: 管理员接口
 * @packagename: com.example.examproject.Controller
 * @author: 姚泽宇
 * @date: 2022-08-13 18:09
 **/
@RestController
@RequestMapping(value = "/admin")
@Slf4j
@Api(tags = "超级管理员权限相关的接口")
public class AdminController {
}
