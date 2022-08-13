package com.example.examproject.Controller;

import com.example.examproject.Utils.createVerificationCode;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @program: examproject
 * @description: 测试
 * @packagename: com.example.examproject.Controller
 * @author: 姚泽宇
 * @date: 2022-08-13 11:49
 **/

@RestController
@RequestMapping("/util")
public class UtilController {


    /**
     * 生成随机验证码图片
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @CrossOrigin
    @RequestMapping("/getCode")
    public void getIdentifyImage(@RequestParam(required = false) String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //设置不缓存图片
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "No-cache");
        response.setDateHeader("Expires", 0);

        //指定生成的响应图片
        response.setContentType("image/jpeg");
        createVerificationCode code = new createVerificationCode();
        BufferedImage image = code.getIdentifyImg();
        HttpSession session = request.getSession(true);
        session.setAttribute("code", code.getCode());
        code.getG().dispose();
        ImageIO.write(image, "JPEG", response.getOutputStream());
    }
}
