package org.softclicker.server.dao.impl;

import org.apache.log4j.Logger;
import org.softclicker.server.dao.DAOUtil;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.Answer;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO extends AbstractGenericDAO<Question> {

    private final static String TABLE_NAME = "QUESTION";
    private final static Logger log = Logger.getLogger(AnswerDAO.class);

    public AnswerDAO(ScopingDataSource scopingDataSource) {
        super(scopingDataSource, TABLE_NAME);
    }

    @Override
    public int count() throws SQLException {
        return 0;
    }

    public List<Answer> getAllAnswers() throws SQLException {
        String sql = "SELECT * FROM `QUESTION` as q,`ANSWER` as a ,`USER` as u WHERE a.OWNER_ID=u.USER_ID AND q.QUESTION_ID=a.QUESTION_ID";
        List<Answer> answers = new ArrayList<>();
        try (
                Connection conn = scopingDataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
        ) {
            while (rs.next()) {
                User ansOwner = DAOUtil.loadUser(rs);
                Question question = DAOUtil.loadQuestion(rs, null);//TODO: questOwner is null
                Answer answer = DAOUtil.loadAnswer(rs, question, ansOwner);
                answers.add(answer);
            }
            return answers;
        }
    }

    public Answer getAnswerById(int answerId) throws SQLException {
        String sql
                = "SELECT * FROM `QUESTION` as q,`ANSWER` as a,`USER` as u WHERE a.OWNER_ID=u.USER_ID AND q.QUESTION_ID=a.QUESTION_ID AND ANSWER_ID=?";
        try (
                Connection conn = scopingDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, answerId);
            try (ResultSet rs = stmt.executeQuery(sql)) {
                User ansOwner = DAOUtil.loadUser(rs);
                Question question = DAOUtil.loadQuestion(rs, null);//TODO: questOwner is null
                return DAOUtil.loadAnswer(rs, question, ansOwner);
            }
        }
    }

    public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException {
        String sql
                = "SELECT * FROM `QUESTION` as q,`ANSWER` as a ,`USER` as u WHERE a.OWNER_ID=u.USER_ID AND q.QUESTION_ID=a.QUESTION_ID AND QUESTION_ID=?";
        List<Answer> answers = new ArrayList<>();
        try (
                Connection conn = scopingDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    User ansOwner = DAOUtil.loadUser(rs);
                    Question question = DAOUtil.loadQuestion(rs, null);//TODO: questOwner is null
                    Answer answer = DAOUtil.loadAnswer(rs, question, ansOwner);
                    answers.add(answer);
                }
                return answers;
            }

        }
    }

}
