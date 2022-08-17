package com.example.examproject.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: examproject
 * @description: 22
 * @packagename: com.example.examproject.Pojo
 * @author: 姚泽宇
 * @date: 2022-08-17 23:01
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankHaveQuestionSum {
    private QuestionBank questionBank;
    //单选数量
    private Integer singleChoice;
    //多选数量
    private Integer multipleChoice;
    //判断数量
    private Integer judge;
    //简答数量
    private Integer shortAnswer;
}
