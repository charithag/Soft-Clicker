package org.softclicker.server.manage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.dao.impl.QuestionDAO;
import org.softclicker.server.dao.impl.UserDAO;
import org.softclicker.server.entity.Question;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionManager {

    private static final Logger log = LogManager.getLogger(QuestionManager.class);
    private final ScopingDataSource scopingDataSource;
    private final QuestionDAO questionDAO;

    public QuestionManager(ScopingDataSource scopingDataSource) {
        this.scopingDataSource = scopingDataSource;
        this.questionDAO = new QuestionDAO(scopingDataSource);
    }

    public List<Question> getAllQuestions() {
        try {
            scopingDataSource.beginConnectionScope();
            return questionDAO.getAllQuestions();
        } catch (SQLException e) {
            log.error("Error while retrieving questions list", e);
            return new ArrayList<>();
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }

    public Question getQuestionById(int questionId) {
        try {
            scopingDataSource.beginConnectionScope();
            return questionDAO.getQuestionById(questionId);
        } catch (SQLException e) {
            log.error("Error occurred while retrieving question with question id: `" + questionId + "`", e);
            return null;
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }
}
