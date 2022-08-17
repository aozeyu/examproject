package com.example.examproject.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: examproject
 * @description: 22
 * @packagename: com.example.examproject.Pojo
 * @author: 姚泽宇
 * @date: 2022-08-17 22:22
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionVo {
    private Integer questionType;
    private Integer questionLevel;
    private Integer[] bankId;
    private String questionContent;
    private String[] images;
    private String analysis;
    private String createPerson;
    private Answer[] Answer;

    //答案对象
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Answer{
        private Integer id;
        private String isTrue;
        private String answer;
        private String[] images;
        private String analysis;
    }
}
