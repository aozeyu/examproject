package com.example.examproject.Pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: examproject
 * @description: 用户实体
 * @packagename: com.example.examproject.Pojo
 * @author: 姚泽宇
 * @date: 2022-08-13 14:03
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户实体")
@TableName(value = "user")
public class User implements Serializable {

    //  对应数据库的主键(uuid,自增id,雪花算法, redis,zookeeper)
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键 用户id", example = "1")
    private Integer id;
    @ApiModelProperty(value = "用户角色id", example = "1(学生) 2(教师) 3(管理员)")
    private Integer roleId;
    @ApiModelProperty(value = "登录用户名", example = "wzz")
    private String username;
    @ApiModelProperty(value = "真实姓名", example = "wzz")
    private String trueName;
    @ApiModelProperty(value = "密码", example = "wzzzz")
    private String password;
    @ApiModelProperty(value = "加密盐值", example = "afada")
    private String salt;
}
