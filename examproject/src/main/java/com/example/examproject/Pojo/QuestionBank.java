package com.example.examproject.Pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: examproject
 * @description: 222
 * @packagename: com.example.examproject.Pojo
 * @author: 姚泽宇
 * @date: 2022-08-16 01:00
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("题库实体")
@TableName(value = "question_bank")
public class QuestionBank {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键 id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "题库id", example = "1")
    private Integer bankId;

    @ApiModelProperty(value = "题库名称", example = "小学数学")
    private String bankName;
}
