package com.example.examproject.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.examproject.Pojo.CommonResult;
import com.example.examproject.Pojo.TokenVo;
import com.example.examproject.Pojo.User;
import com.example.examproject.Pojo.UserRole;
import com.example.examproject.Service.impl.UserRoleServiceImpl;
import com.example.examproject.Service.impl.UserServiceImpl;
import com.example.examproject.Utils.CheckToken;
import com.example.examproject.Utils.SaltEncryption;
import com.example.examproject.Utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
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
@Slf4j
@Api(tags = "(学生,教师,管理员)通用相关接口",description = "提供相关的 Rest API")
public class CommonController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRoleServiceImpl userRoleService;
    @ApiOperation("用户注册接口")
    @PostMapping("/register")
    public CommonResult<String> Register(@RequestBody User user) throws NoSuchAlgorithmException {
        log.info("执行了===>CommonController中的register方法");
        String salt = UUID.randomUUID().toString().substring(0,6);
        String newPwd = SaltEncryption.saltEncryption(user.getPassword(), salt);
        user.setPassword(newPwd);
        user.setSalt(salt);

        user.setCreateDate(new Date());
        //设置加密
        userService.save(user);
        //发放token令牌
        String token = TokenUtils.createToken(new TokenVo("1", user.getUsername(), newPwd));
        return new CommonResult<String>(200,"success",token);
    }
    @GetMapping("/check/{username}")
    @ApiOperation("用户名合法查询接口")
    public CommonResult<String> checkUsername(@PathVariable("username") String username) {
        log.info("执行了===>CommonController中的checkUsername方法");
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
    @ApiOperation("用户登录接口")
    public CommonResult<String> login(@RequestBody User user) throws NoSuchAlgorithmException {
        log.info("执行了===>CommonController中的login方法");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        //user1 是匹配查出来的用户
        User user1 = userService.getOne(wrapper);
        if (user1 != null) {
            //对用户输入的密码加密后 对比数据库的密码
//            String newPwd = SaltEncryption.saltEncryption(user1.getPassword(), user1.getSalt());
            String newPwd = user1.getPassword();
            if (newPwd.equals(user1.getPassword()) && user1.getStatus() == 1) {
                //发放token令牌
                String token = TokenUtils.createToken(new TokenVo(user1.getRoleId() + "", user1.getUsername(), newPwd));
                return new CommonResult<>(200, "success", token);
            } else {//密码错误
                return new CommonResult<>(233, "账号密码错误,或账号被封");
            }
        } else return new CommonResult<>(233, "用户不存在");
    }

    @GetMapping("/getMenu")
    @ApiOperation(value = "获取不同用户的权限菜单")
    public CommonResult<String> getMenu(HttpServletRequest request) {
        log.info("执行了===>CommonController中的getMenu方法");
        //工具类验证token是否有效 有效返回tokenVo对象,否则返回null
        TokenVo tokenVo = new CheckToken().checkToken(request, userService);
        if (tokenVo != null) {
            String roleId =tokenVo.getRoleId();
            QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
            wrapper.eq("role_id",roleId);
            UserRole userRole = userRoleService.getOne(wrapper);
            return new CommonResult<>(200, "success", userRole.getMenuInfo());
        }else {
            return new CommonResult<>(233, "认证信息有误,获取数据失败");
        }
    }

    @GetMapping("/checkToken")
    @ApiOperation("验证用户token接口")
    public CommonResult<TokenVo> checkToken(HttpServletRequest request) {
        log.info("执行了===>CommonController中的checkToken方法");
        TokenVo tokenVo = new CheckToken().checkToken(request,userService);
        if (tokenVo != null) {//有效token
            return new CommonResult<>(200, "success", tokenVo);
        } else {
            return new CommonResult<>(233, "token无效");
        }
    }
    @GetMapping("/logout")
    @ApiOperation(value = "用户主动退出登录")
    public CommonResult<String> logout() {
        log.info("执行了===>CommonController中的logout方法");
        return new CommonResult<>(200,"退出成功");
    }
}
