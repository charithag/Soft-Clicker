package org.softclicker.server.entity;

import java.util.Date;

/**
 * This class acts as the Question entity and intends to use as an immutable object.
 * You can create new {@link Question} only by passing a parameters through the constructor.
 */
public class Question {
    final private int questionId;
    final private String question;
    final private String answer;
    final private User owner;
    final private Date createdTime;
    final private Date expireTime;

    public Question(int questionId, String question, String answer, User owner, Date createdTime, Date expireTime) {
        this.questionId = questionId;
        this.question = question;
        this.answer = answer;
        this.owner = owner;
        this.createdTime = createdTime;
        this.expireTime = expireTime;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public User getOwner() {
        return owner;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    @Override
    public String toString() {
        return "Question: " + getQuestion() + "\tAnswer: " + getAnswer() + "\tCreated By: " + getOwner()
                .getUserName() + "\tCreated On: " + getCreatedTime().toString() + "\tExpires On: "
                + getExpireTime().toString();
    }
}
