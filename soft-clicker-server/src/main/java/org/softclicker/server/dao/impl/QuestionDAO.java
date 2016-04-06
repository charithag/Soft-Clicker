package org.softclicker.server.dao.impl;

import org.apache.log4j.Logger;
import org.softclicker.server.dao.DAOUtil;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public List<Question> getAllQuestions(UserDAO userDAO) throws SQLException{
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

    public Question getQuestionById(int questionId) throws SQLException{
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
}
