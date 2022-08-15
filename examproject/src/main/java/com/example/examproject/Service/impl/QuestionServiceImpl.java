package com.example.examproject.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.examproject.Mapper.QuestionMapper;
import com.example.examproject.Pojo.Question;
import com.example.examproject.Service.QuestionService;
import org.springframework.stereotype.Service;

/**
 * @program: examproject
 * @description: 222
 * @packagename: com.example.examproject.Service.impl
 * @author: 姚泽宇
 * @date: 2022-08-16 00:28
 **/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
}