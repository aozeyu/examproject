package com.example.examproject.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @program: examproject
 * @description: 222
 * @packagename: com.example.examproject.Pojo
 * @author: 姚泽宇
 * @date: 2022-08-19 22:39
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddExamVo {
    private String examName;
    private String examDesc;
    private Integer type;
    private String password;
    private Integer duration;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    private Integer totalScore;
    private Integer passScore;
    private Integer status;


    private String questionIds;
    private Integer examId;
    private String scores;
}
