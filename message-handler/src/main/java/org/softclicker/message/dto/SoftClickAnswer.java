package org.softclicker.message.dto;

public class SoftClickAnswer {

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