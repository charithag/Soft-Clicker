package org.softclicker.server.starup;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.config.ServerConfigManager;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.Answer;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.gui.MainApplication;
import org.softclicker.server.manage.AnswerManager;
import org.softclicker.server.manage.ClazzManager;
import org.softclicker.server.manage.QuestionManager;
import org.softclicker.server.manage.UserManager;

public class Bootstrap {

    private final static Logger log = LogManager.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        ScopingDataSource scopingDataSource = new ScopingDataSource(new ServerConfigManager());
        DatabaseCreator creator = new DatabaseCreator(scopingDataSource);
        creator.createDbStructureIfNotExists();
        UserManager userManager = new UserManager(scopingDataSource);
        QuestionManager questionManager = new QuestionManager(scopingDataSource);
        AnswerManager answerManager = new AnswerManager(scopingDataSource);
        ClazzManager clazzManager = new ClazzManager(scopingDataSource);
        //read all users
        try {
            log.info("-----------------Users-------------------");
            for (User user : userManager.getAllUsers()) {
                log.info(user);
            }
        } catch (SoftClickerException e) {
            log.error("Error occurred while retrieving User records.", e);
        }
        //read all questions
        try {
            log.info("---------------Questions-----------------");
            for (Question question : questionManager.getAllQuestions()) {
                log.info(question);
            }
        } catch (SoftClickerException e) {
            log.error("Error occurred while retrieving Question records.", e);
        }
        //read all answers
        try {
            log.info("----------------Answers------------------");
            for (Answer answer : answerManager.getAllAnswers()) {
                log.info(answer);
            }
        } catch (SoftClickerException e) {
            log.error("Error occurred while retrieving Answer records.", e);
        }
//        questionManager.getQuestionsByClass("NETWORKING");
        log.info("SoftClicker Server Started!");
        // Start UI application
        MainApplication app = MainApplication.getInstance();
        app.setAnswerManager(answerManager);
        app.setQuestionManager(questionManager);
        app.setUserManager(userManager);
        app.setClazzManager(clazzManager);
        app.main(args);
        log.info("SoftClicker UI Finished!");
    }
}
