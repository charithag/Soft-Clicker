package org.softclicker.server.dao.impl;

import org.apache.log4j.Logger;
import org.softclicker.server.dao.DAOUtil;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.Answer;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO extends AbstractGenericDAO<Question> {

    private final static String TABLE_NAME = "ANSWER";
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
                = "SELECT * FROM `QUESTION` as q,`ANSWER` as a ,`USER` as u WHERE a.OWNER_ID=u.USER_ID AND q.QUESTION_ID=a.QUESTION_ID AND q.QUESTION_ID=?";
        List<Answer> answers = new ArrayList<>();
        try (
                Connection conn = scopingDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery()) {
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

    public boolean saveAnswer(Answer answer) {
        String sql = "INSERT INTO `" + TABLE_NAME + "` (`ANSWER`, `QUESTION_ID`, `OWNER_ID`, `ANSWERED_TIME`) values (?,?,?,?)";
        try (
                Connection conn = scopingDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            int count = 0;
            stmt.setString(++count, answer.getAnswer());
            stmt.setInt(++count, answer.getQuestion().getQuestionId());
            if (answer.getOwner() != null) {
                stmt.setInt(++count, answer.getOwner().getUserId());
            } else {
                //TODO set default ID
                stmt.setInt(++count, 1);
            }

            stmt.setTimestamp(++count, new Timestamp(answer.getAnsweredTime().getTime()));

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            answer.setAnswerId(id);
            return true;
        } catch (SQLException e) {
            log.error("Unable to save Answer", e);
            return false;
        }
    }

}
