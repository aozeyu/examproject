package com.example.examproject.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: examproject
 * @description: 222
 * @packagename: com.example.examproject.Pojo
 * @author: 姚泽宇
 * @date: 2022-08-13 13:41
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenVo {
    private String roleId;
    private String username;
    private String password;
}
