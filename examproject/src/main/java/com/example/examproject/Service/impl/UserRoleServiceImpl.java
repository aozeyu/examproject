package com.example.examproject.Service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.examproject.Mapper.UserRoleMapper;
import com.example.examproject.Pojo.UserRole;
import com.example.examproject.Service.UserRoleService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @program: examproject
 * @description: 222
 * @packagename: com.example.examproject.Service.impl
 * @author: 姚泽宇
 * @date: 2022-08-13 18:19
 **/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
