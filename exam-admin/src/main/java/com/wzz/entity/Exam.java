package com.wzz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Date 2020/11/1 16:05
 * @created by wzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("考试实体")
@TableName(value = "exam")
public class Exam {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键 考试id", example = "1")
    private Integer examId;

    @ApiModelProperty(value = "考试名称", example = "小学一年级考试")
    private String name;

    @ApiModelProperty(value = "考试类型1公开,2需密码", example = "1")
    private Integer type;

    @ApiModelProperty(value = "考试密码,当type=2时候存在", example = "12345")
    private String password;

    @ApiModelProperty(value = "考试开始时间", example = "2020-11-01")
    private Date startTime;

    @ApiModelProperty(value = "考试结束时间", example = "2020-12-01")
    private Date endTime;

    @ApiModelProperty(value = "考试总分", example = "100")
    private Integer totalScore;

    @ApiModelProperty(value = "考试及格线", example = "60")
    private Integer passScore;

    @ApiModelProperty(value = "考试状态 1有效 2无效", example = "1")
    private Integer status;
}
