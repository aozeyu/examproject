package com.example.examproject.Pojo;

/**
 * @program: examproject
 * @description: 222
 * @packagename: com.example.examproject.Pojo
 * @author: 姚泽宇
 * @date: 2022-08-19 22:43
 **/
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("考试里的题目实体")
@TableName(value = "exam_question")
public class ExamQuestion implements Serializable{
    //  对应数据库的主键(uuid,自增id,雪花算法, redis,zookeeper)
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键 考试题目表的id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "问题的id字符串", example = "1,2,3")
    private String questionIds;

    @ApiModelProperty(value = "考试的id",example = "1")
    private Integer examId;

    @ApiModelProperty(value = "考试中每一题的分数",example = "1,2,3")
    private String scores;
}
