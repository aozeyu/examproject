package com.example.examproject.Utils;

import org.springframework.util.DigestUtils;

import java.security.NoSuchAlgorithmException;

/**
 * @program: examproject
 * @description: 加密
 * @packagename: com.example.examproject.Utils
 * @author: 姚泽宇
 * @date: 2022-08-13 13:24
 **/
public class SaltEncryption {
    public static String saltEncryption(String password, String salt) throws NoSuchAlgorithmException {
        String current = password + salt;
        return DigestUtils.md5DigestAsHex(current.getBytes());

    }
}
