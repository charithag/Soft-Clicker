package org.softclicker.server.manage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.dao.impl.QuestionDAO;
import org.softclicker.server.entity.Clazz;
import org.softclicker.server.entity.Question;
import org.softclicker.server.exception.SoftClickerException;

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

    public List<Question> getAllQuestions() throws SoftClickerException {
        try {
            scopingDataSource.beginConnectionScope();
            return questionDAO.getAllQuestions();
        } catch (SQLException e) {
            throw new SoftClickerException("Error while retrieving questions list", e);
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }

    public Question getQuestionById(int questionId) throws SoftClickerException {
        try {
            scopingDataSource.beginConnectionScope();
            return questionDAO.getQuestionById(questionId);
        } catch (SQLException e) {
            throw new SoftClickerException("Error occurred while retrieving question with question id: '" + questionId + "'", e);
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }

    public List<Question> getQuestionsByClass(String className) throws SoftClickerException {
        try {
            scopingDataSource.beginConnectionScope();
            return questionDAO.getQuestionsByClass(className);
        } catch (SQLException e) {
            throw new SoftClickerException("Error occurred while retrieving question with class name: '" + className + "'", e);
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }

    public List<Question> getQuestionsByClassID(int classID) throws SoftClickerException {
        try {
            scopingDataSource.beginConnectionScope();
            return questionDAO.getQuestionsByClassID(classID);
        } catch (SQLException e) {
            throw new SoftClickerException("Error occurred while retrieving question with class ID: '" + classID + "'", e);
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }

    public List<Clazz> getValidClasses()
    {
        try {
            scopingDataSource.beginConnectionScope();
            return questionDAO.getValidClasses();
        } catch (SQLException e) {
            log.error("Error occurred while retrieving all the answered classes", e);
            return null;
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }

    public boolean saveQuestion(Question question) {
        try {
            scopingDataSource.beginConnectionScope();
            return questionDAO.saveQuestion(question);
        } catch (SQLException e) {
            log.error("Error occurred while saving question", e);
            return false;
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }
}
