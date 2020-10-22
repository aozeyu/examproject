package com.wzz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzz.entity.User;
import com.wzz.service.impl.UserServiceImpl;
import com.wzz.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @Date 2020/10/20 19:07
 * @created by wzz
 */
@RestController
@RequestMapping(value = "/admin")
@Slf4j
@Api(tags = "超级管理员权限相关的接口")
public class AdminController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/getUser")
    @ApiOperation("获取用户信息,可分页 ----> 查询条件(可无)(username,trueName),必须有的(pageNo,pageSize)")
    public CommonResult<List<User>> getUser(@RequestParam(required = false) String loginName,
                                            @RequestParam(required = false) String trueName,
                                            Integer pageNo, Integer pageSize) {
        log.info("执行了===>AdminController中的getUser方法");
        //参数一是当前页，参数二是每页个数
        IPage<User> userPage = new Page<>(pageNo, pageSize);
        //查询条件(可选)
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (!Objects.equals(loginName, "")) wrapper.like("username", loginName);
        if (!Objects.equals(trueName, "")) wrapper.like("true_name", trueName);

        userPage = userService.page(userPage, wrapper);
        List<User> users = userPage.getRecords();
        return new CommonResult<List<User>>(200, "success", users);
    }

}
