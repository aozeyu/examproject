package com.wzz.Util;

import org.springframework.util.DigestUtils;
import java.security.NoSuchAlgorithmException;

public class SaltEncryption {

    //盐值加密
    public static String saltEncryption(String password, String salt) throws NoSuchAlgorithmException {
        String current = password + salt;
        return DigestUtils.md5DigestAsHex(current.getBytes());
    }
}
