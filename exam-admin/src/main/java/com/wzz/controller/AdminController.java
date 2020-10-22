package com.wzz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzz.entity.User;
import com.wzz.service.impl.UserServiceImpl;
import com.wzz.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/handleUser/{type}")
    @ApiOperation("管理员操作用户: type=1(启用) 2(禁用) 3(删除) userIds(需要操作的用户id)")
    public CommonResult<String> handleUser(@PathVariable("type")Integer type,String userIds){
        log.info("执行了===>AdminController中的handleUser方法");
        //转换成数组 需要操作的用户的id数组
        String[] ids = userIds.split(",");
        if (type == 1){//启用
            for (String id : ids) {
                //当前需要修改的用户
                User user = userService.getById(Integer.parseInt(id));
                user.setStatus(1);//设置为启用的用户
                userService.update(user, new UpdateWrapper<User>().eq("id", Integer.parseInt(id)));
            }
            return new CommonResult<>(200,"操作成功");
        }else if(type == 2) {//禁用
            for (String id : ids) {
                //当前需要修改的用户
                User user = userService.getById(Integer.parseInt(id));
                user.setStatus(2);//设置为禁用的用户
                userService.update(user, new UpdateWrapper<User>().eq("id", Integer.parseInt(id)));
            }
            return new CommonResult<>(200,"操作成功");
        }else if (type == 3){//删除
            for (String id : ids) {
                userService.removeById(Integer.parseInt(id));
            }
            return new CommonResult<>(200,"操作成功");
        }else return new CommonResult<>(233,"操作有误");
    }


}
