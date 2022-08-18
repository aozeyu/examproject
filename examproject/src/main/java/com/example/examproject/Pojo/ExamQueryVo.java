package com.example.examproject.Pojo;

/**
 * @program: examproject
 * @description: 22
 * @packagename: com.example.examproject.Pojo
 * @author: 姚泽宇
 * @date: 2022-08-19 00:24
 **/
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamQueryVo {
    private Integer examType;
    private String startTime;
    private String endTime;
    private String examName;
    private Integer pageNo;
    private Integer pageSize;
}
