package com.example.examproject;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.examproject.Pojo.User;
import com.example.examproject.Pojo.UserRole;
import com.example.examproject.Service.impl.UserRoleServiceImpl;
import com.example.examproject.Utils.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class ExamprojectApplicationTests {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Test
    void testRedis() throws JsonProcessingException {
        User user = new User(1, 1, "wzz", "112", "1231", "121", 1, new Date());
        System.out.println(user);
        ObjectMapper mapper = new ObjectMapper();
        redisUtil.set("user:1", mapper.writeValueAsString(user), 60);
        System.out.println(mapper.readValue(redisUtil.get("user:1").toString(), User.class));
    }

    @Test
    void t2() {
        System.out.println(redisUtil.get("userRoles"));
        List<UserRole> userRoles = userRoleService.list(new QueryWrapper<>());
        redisUtil.set("userRoles", userRoles);
    }
}
