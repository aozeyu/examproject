package com.example.examproject.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.examproject.Pojo.ExamQuestion;
import org.springframework.stereotype.Repository;

/**
 * @program: examproject
 * @description: 222
 * @packagename: com.example.examproject.Mapper
 * @author: 姚泽宇
 * @date: 2022-08-19 22:44
 **/
@Repository//代表持久层
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {
}