package com.softclicker.message.dto;

/**
 * Created by charitha on 3/6/16.
 */
public class SCAnswer {

    private String studentId;
    private AnswerOption answerOption;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public AnswerOption getAnswerOption() {
        return answerOption;
    }

    public void setAnswerOption(AnswerOption answerOption) {
        this.answerOption = answerOption;
    }

    public enum AnswerOption {
        OPTION_1, OPTION_2, OPTION_3, OPTION_4, OPTION_5;
    }
}
