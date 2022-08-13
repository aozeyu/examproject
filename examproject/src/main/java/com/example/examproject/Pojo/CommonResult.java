package com.example.examproject.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: examproject
 * @description: 通用结果类
 * @packagename: com.example.examproject.Pojo
 * @author: 姚泽宇
 * @date: 2022-08-13 14:00
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T>{
    private Integer code;
    private String message;
    private T data;
    public CommonResult(Integer code, String message) {
        this(code, message, null);
    }
}
