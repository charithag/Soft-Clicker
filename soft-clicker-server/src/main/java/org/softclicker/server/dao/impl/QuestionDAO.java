package org.softclicker.server.dao.impl;

import org.apache.log4j.Logger;
import org.softclicker.server.dao.DAOUtil;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.Clazz;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO extends AbstractGenericDAO<Question> {

    private final static String TABLE_NAME = "QUESTION";
    private final static Logger log = Logger.getLogger(QuestionDAO.class);

    public QuestionDAO(ScopingDataSource scopingDataSource) {
        super(scopingDataSource, TABLE_NAME);
    }

    @Override
    public int count() throws SQLException {
        return 0;
    }

    public List<Question> getAllQuestions() throws SQLException {
        String sql = "SELECT * FROM `QUESTION` as q,`USER` as u WHERE q.OWNER_ID=u.USER_ID";
        List<Question> questions = new ArrayList<>();
        try (
                Connection conn = scopingDataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
        ) {
            while (rs.next()) {
                User user = DAOUtil.loadUser(rs);
                Question question = DAOUtil.loadQuestion(rs, user);
                questions.add(question);
            }
            return questions;
        }
    }

    public Question getQuestionById(int questionId) throws SQLException {
        String sql = "SELECT * FROM `QUESTION` as q,`USER` as u WHERE q.OWNER_ID=u.USER_ID AND QUESTION_ID=?";
        try (
                Connection conn = scopingDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery(sql)) {
                User owner = DAOUtil.loadUser(rs);
                return DAOUtil.loadQuestion(rs, owner);
            }
        }
    }

    public List<Question> getQuestionsByClass(String className) throws SQLException {
        String sql = "SELECT * FROM `QUESTION` as q,`USER` as u, `CLASS` as c WHERE q.OWNER_ID=u.USER_ID AND c.CLASS_ID=Q.CLASS_ID AND CLASS_NAME=?";
        List<Question> questions = new ArrayList<>();
        try (
                Connection conn = scopingDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setString(1, className);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = DAOUtil.loadUser(rs);
                    Question question = DAOUtil.loadQuestion(rs, user);
                    questions.add(question);
                }
            }
            return questions;
        }
    }

    public List<Question> getQuestionsByClassID(int classID) throws SQLException {
        String sql = "SELECT * FROM `QUESTION` as q,`USER` as u, `CLASS` as c WHERE q.OWNER_ID=u.USER_ID AND c.CLASS_ID=Q.CLASS_ID AND c.CLASS_ID=?";
        List<Question> questions = new ArrayList<>();
        try (
                Connection conn = scopingDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, classID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = DAOUtil.loadUser(rs);
                    Question question = DAOUtil.loadQuestion(rs, user);
                    questions.add(question);
                }
            }
            return questions;
        }
    }

    /**
     * Retrieve classes that have questions
     *
     * @return
     * @throws SQLException
     */
    public List<Clazz> getValidClasses() throws SQLException {
        String sql = "SELECT DISTINCT c.CLASS_ID,c.CLASS_NAME,c.CLASS_YEAR FROM `QUESTION` as q,`USER` as u, `CLASS` as c WHERE q.OWNER_ID=u.USER_ID AND c.CLASS_ID=Q.CLASS_ID ";
        List<Clazz> clazzes = new ArrayList<>();
        try (
                Connection conn = scopingDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Clazz clazz = DAOUtil.loadClass(rs, null);
                    clazzes.add(clazz);
                }
            }
            return clazzes;
        }
    }

    public boolean saveQuestion(Question question) {
        String sql = "INSERT INTO " + TABLE_NAME + " (`QUESTION`, `CORRECT_ANSWER`, `OWNER_ID`, `CLASS_ID`, `CREATED_TIME`, `EXPIRE_TIME`) values(?, ?, ?, ?, ?, ?)";
        try (
                Connection conn = scopingDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            int count = 0;
            stmt.setString(++count, question.getQuestion());
            stmt.setString(++count, question.getAnswer());
            if (question.getOwner() != null) {
                stmt.setInt(++count, question.getOwner().getUserId());
            } else {
                //TODO set default ID
                stmt.setInt(++count, 1);
            }
            stmt.setInt(++count, question.getClassID());
            stmt.setTimestamp(++count, new Timestamp(question.getCreatedTime().getTime()));
            stmt.setTimestamp(++count, new Timestamp(question.getExpireTime().getTime()));

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            question.setQuestionId(id);
            return true;
        } catch (SQLException e) {
            log.error("Unable to save Class details", e);
            return false;
        }
    }
}
