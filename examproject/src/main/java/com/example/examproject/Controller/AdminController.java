package com.example.examproject.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.examproject.Pojo.CommonResult;
import com.example.examproject.Pojo.User;
import com.example.examproject.Service.impl.UserServiceImpl;
import com.example.examproject.Utils.RedisUtil;
import com.example.examproject.Utils.SaltEncryption;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RedisUtil redisUtil;
    ObjectMapper mapper = new ObjectMapper();
    @GetMapping("/getUser")
    @ApiOperation("获取用户信息,可分页 ----> 查询条件(可无)(username,trueName),必须有的(pageNo,pageSize)")
    public CommonResult<List<User>> getUser(
            @RequestParam(required = false) String loginName,
            @RequestParam(required = false) String trueName,
            Integer pageNo, Integer pageSize
    ) {
        log.info("执行了===>AdminController中的getUser方法");
        //参数一是当前页，参数二是每页个数
        IPage<User> userPage = new Page<>(pageNo, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //不为空就模糊查询
        if (!Objects.equals(loginName, "")) wrapper.like("username", loginName);
        if (!Objects.equals(trueName, "")) wrapper.like("true_name", trueName);
        userPage = userService.page(userPage, wrapper);
        List<User> users = userPage.getRecords();
        return new CommonResult<>(200, "success", users);
    }
    @GetMapping("/handleUser/{type}")
    @ApiOperation("管理员操作用户: type=1(启用) 2(禁用) 3(删除) userIds(需要操作的用户id)")
    public CommonResult<String> handleUser(@PathVariable("type")Integer type, String userIds) {

        log.info("执行了===>AdminController中的handleUser方法");
        String[] ids = userIds.split(",");
        if (type == 1) {
            for (String id:ids) {
                User user = userService.getById(Integer.parseInt(id));
                user.setStatus(1);
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

    @PostMapping("/addUser")
    @ApiOperation("管理员用户新增用户")
    public CommonResult<String> addUser(@RequestBody User user) throws NoSuchAlgorithmException {
        log.info("执行了===>AdminController中的addUser方法");
        //盐值

        String salt = UUID.randomUUID().toString().substring(0,6);
        String newPwd= SaltEncryption.saltEncryption(user.getPassword(),salt);
        user.setPassword(newPwd);
        user.setSalt(salt);
        user.setCreateDate(new Date());
        boolean save = userService.save(user);
        return save ? new CommonResult<>(200,"操作成功") : new CommonResult<>(233,"操作失败");
    }
}
