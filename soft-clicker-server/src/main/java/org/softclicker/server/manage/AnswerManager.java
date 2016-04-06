package org.softclicker.server.manage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.dao.impl.AnswerDAO;
import org.softclicker.server.entity.Answer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnswerManager {

    private static final Logger log = LogManager.getLogger(AnswerManager.class);
    private final ScopingDataSource scopingDataSource;
    private final AnswerDAO answerDAO;

    public AnswerManager(ScopingDataSource scopingDataSource) {
        this.scopingDataSource = scopingDataSource;
        this.answerDAO = new AnswerDAO(scopingDataSource);
    }

    public List<Answer> getAllAnswers() {
        try {
            scopingDataSource.beginConnectionScope();
            return answerDAO.getAllAnswers();
        } catch (SQLException e) {
            log.error("Error while retrieving answers list", e);
            return new ArrayList<>();
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }

    public Answer getAnswerById(int answerId) {
        try {
            scopingDataSource.beginConnectionScope();
            return answerDAO.getAnswerById(answerId);
        } catch (SQLException e) {
            log.error("Error occurred while retrieving answer with answer id: `" + answerId + "`", e);
            return null;
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }

    public List<Answer> getAnswersByQuestionId(int questionId) {
        try {
            scopingDataSource.beginConnectionScope();
            return answerDAO.getAnswersByQuestionId(questionId);
        } catch (SQLException e) {
            log.error("Error while retrieving answers list for question id: `" + questionId + "`", e);
            return new ArrayList<>();
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }
}
