package com.example.examproject.Controller;

import com.example.examproject.Pojo.CommonResult;
import com.example.examproject.Utils.createVerificationCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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
@Api(tags = "工具类接口")
public class UtilController {


    static String CODE;
    /**
     * 生成随机验证码图片
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @CrossOrigin
    @GetMapping("/getCodeImg")
    @ApiOperation(value = "获取验证码图片流")
    public void getIdentifyImage(@RequestParam(required = false) String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //设置不缓存图片
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "No-cache");
        response.setDateHeader("Expires", 0);

        //指定生成的响应图片
        response.setContentType("image/jpeg");
        createVerificationCode code = new createVerificationCode();
        BufferedImage image = code.getIdentifyImg();
        code.getG().dispose();
        ImageIO.write(image, "JPEG", response.getOutputStream());
        CODE = code.getCode();
    }
    @GetMapping("/getCode")
    @ApiOperation(value = "获取验证码")
    public CommonResult<String> getCode() {
        return new CommonResult<>(200, CODE);
    }
}
