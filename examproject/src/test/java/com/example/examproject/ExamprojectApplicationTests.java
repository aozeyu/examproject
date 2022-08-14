package com.example.examproject;

import com.example.examproject.Pojo.User;
import com.example.examproject.Utils.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class ExamprojectApplicationTests {
    @Autowired
    private RedisUtil redisUtil;
    @Test
    void testRedis() throws JsonProcessingException {
        User user = new User(1, 1, "wzz", "112", "1231", "121", 1, new Date());
        System.out.println(user);
        ObjectMapper mapper = new ObjectMapper();
        redisUtil.set("user:1", mapper.writeValueAsString(user),60);
        System.out.println(mapper.readValue(redisUtil.get("user:1").toString(),User.class));
    }

}
