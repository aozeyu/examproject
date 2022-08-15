package com.example.examproject.Pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: examproject
 * @description: 222
 * @packagename: com.example.examproject.Pojo
 * @author: 姚泽宇
 * @date: 2022-08-15 23:42
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("题目实体")
@TableName(value = "question")
public class Question {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键 题目id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "问题内容", example = "1+1等于几")
    private String quContent;

    @ApiModelProperty(value = "创建时间", example = "2020-10-24 14:58")
    private Date createTime;

    @ApiModelProperty(value = "创建人的id", example = "1")
    private Integer createPerson;

    @ApiModelProperty(value = "问题类型", example = "选择 多选 判断  简答")
    private Integer quType;

    @ApiModelProperty(value = "问题难度", example = "1")
    private Integer level;

    @ApiModelProperty(value = "问题相关的图片", example = "imageUrl")
    private String image;

    @ApiModelProperty(value = "所属题库的id", example = "1")
    private Integer quBank;

    @ApiModelProperty(value = "题目解析", example = "题目解析")
    private String analysis;
}
