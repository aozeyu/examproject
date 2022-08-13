package com.example.examproject.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.examproject.Pojo.CommonResult;
import com.example.examproject.Pojo.TokenVo;
import com.example.examproject.Pojo.User;
import com.example.examproject.Service.impl.UserServiceImpl;
import com.example.examproject.Utils.SaltEncryption;
import com.example.examproject.Utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @program: examproject
 * @description: 通用接口
 * @packagename: com.example.examproject.Controller
 * @author: 姚泽宇
 * @date: 2022-08-13 13:54
 **/
@RestController
@RequestMapping(value = "/common")
@Api(tags = "(学生,教师,管理员)通用相关接口",description = "提供相关的 Rest API")
public class CommonController {
    @Autowired
    private UserServiceImpl userService;
    @ApiOperation("用户注册接口")
    @PostMapping("/register")
    public CommonResult<String> Register(@RequestBody User user) throws NoSuchAlgorithmException {
        String salt = UUID.randomUUID().toString().substring(0,6);
        String newPwd = SaltEncryption.saltEncryption(user.getPassword(), salt);
        user.setPassword(newPwd);
        user.setSalt(salt);
        //设置加密
        userService.save(user);
        //发放token令牌
        String token = TokenUtils.createToken(new TokenVo(user.getRoleId() + "", user.getUsername(), user.getPassword()), 3000);
        return new CommonResult<String>(200,"success",token);
    }
    @GetMapping("/check/{username}")
    @ApiOperation("用户名合法查询接口")
    public CommonResult<String> checkUsername(@PathVariable("username") String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //命中username和输入的哪一个对应的
        wrapper.eq("username",username);
        User user = userService.getOne(wrapper);
        if (user == null) {
            return new CommonResult<>(200,"用户名可以使用");
        }else {
            return new CommonResult<>(233,"用户已存在");
        }
    }

    @PostMapping("/login")
    @ApiOperation("登陆接口")
    public CommonResult<String> login(@RequestParam(value = "username") String username,@RequestParam(value = "password") String password) throws NoSuchAlgorithmException{
        System.out.println(username);
        System.out.println(password);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        User user = userService.getOne(wrapper);
        if (user != null) {
            String newPwd = SaltEncryption.saltEncryption(password, user.getSalt());
            if (newPwd.equals(user.getPassword())) {
                //发放token令牌
                String token = TokenUtils.createToken(new TokenVo(user.getRoleId() + "", user.getUsername(), user.getPassword()), 3000);
                return new CommonResult<>(200, "success", token);
            }else {
                return new CommonResult<>(233, "账号密码错误");
            }
        }else return new CommonResult<>(233,"用户不存在");
    }
}
