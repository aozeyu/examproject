package com.example.examproject.Utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.examproject.Pojo.TokenVo;
import com.example.examproject.Pojo.User;
import com.example.examproject.Service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @program: examproject
 * @description: 222
 * @packagename: com.example.examproject.Utils
 * @author: 姚泽宇
 * @date: 2022-08-13 17:24
 **/
public class CheckToken {
    public TokenVo checkToken(HttpServletRequest request, UserServiceImpl userService) {
        //获取用户的头部信息的token
        String token = request.getHeader("authorization");
        if (token != null) {
            //获取解析后的token令牌
            TokenVo tokenVo = TokenUtils.verifyToken(token);
            if (tokenVo != null) {//解析token是否过期
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.eq("username", tokenVo.getUsername());
                User user = userService.getOne(wrapper);
                //校验token是否合法 并且是否过期
                if (tokenVo != null && user != null && user.getRoleId() == Integer.parseInt(tokenVo.getRoleId()) && Objects.equals(user.getPassword(), tokenVo.getPassword()) && user.getStatus() == 1) {
                    return tokenVo;
                } else {//非法token
                    return null;
                }
            } else {
                return null;
            }
        } else {//没有token
            return null;
        }
    }
}
