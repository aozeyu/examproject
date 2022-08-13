package com.example.examproject.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.examproject.Pojo.TokenVo;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: examproject
 * @description: token认证
 * @packagename: com.example.examproject.Utils
 * @author: 姚泽宇
 * @date: 2022-08-13 13:27
 **/
public class TokenUtils {
    private static final long EXPIRE_TIME = 15 * 60 * 1000;//默认15分钟
    //私钥
    private static final String TOKEN_SECRET = "yaozeyu";

    /**
     * 生成token，自定义过期时间 毫秒
     *
     * @param **username**
     * @param **password**
     * @return
     */


    public static String createToken(TokenVo token) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            Map<String, Object> header = new HashMap<>(2);
            header.put("Type","Jwt");
            header.put("alg","HS256");
            return JWT.create().withHeader(header)
                    .withClaim("roleId", token.getRoleId())
                    .withClaim("username", token.getUsername())
                    .withClaim("password", token.getPassword()).withExpiresAt(date).sign(algorithm);
        }catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    /**
     * 检验token是否正确
     *
     * @param **token**
     * @return
     */

    public static TokenVo verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String roleId = jwt.getClaim("roleId").asString();
            String username = jwt.getClaim("username").asString();
            String password = jwt.getClaim("password").asString();
            return new TokenVo(roleId,username,password);
        }catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String token = TokenUtils.createToken(new TokenVo("1","wzz","123"));
        System.out.println("token == " + token);
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        System.out.println(tokenVo);
    }
 }
