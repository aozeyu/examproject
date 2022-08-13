package com.example.examproject.Config;

import com.example.examproject.Pojo.TokenVo;
import com.example.examproject.Service.impl.UserServiceImpl;
import com.example.examproject.Utils.CheckToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @program: examproject
 * @description: 222
 * @packagename: com.example.examproject.Config
 * @author: 姚泽宇
 * @date: 2022-08-13 21:47
 **/
@Component
public class AdminInterceptor {
    @Autowired
    private UserServiceImpl userService;

    //这个方法是在访问接口之前执行的，我们只需要在这里写验证登陆状态的业务逻辑，就可以在用户调用指定接口之前验证登陆状态了
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断用户的token信息是否满足
        TokenVo tokenVo = new CheckToken().checkToken(request, userService);
        if (tokenVo != null && Objects.equals(tokenVo.getRoleId(), 3 + "")){
            return true;
        }
        //当前不满足条件,直接跳转拦截
        response.getWriter().print("Access denied");
        return false;
    }
}
