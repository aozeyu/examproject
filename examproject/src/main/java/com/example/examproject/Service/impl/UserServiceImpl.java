package com.example.examproject.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.examproject.Mapper.UserMapper;
import com.example.examproject.Pojo.User;
import com.example.examproject.Service.UserService;
import org.springframework.stereotype.Service;

/**
 * @program: examproject
 * @description: 实现类
 * @packagename: com.example.examproject.Service.impl
 * @author: 姚泽宇
 * @date: 2022-08-13 14:02
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService {
}
