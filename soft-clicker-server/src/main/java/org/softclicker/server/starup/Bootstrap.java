package org.softclicker.server.starup;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.config.ServerConfigManager;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.Answer;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;
import org.softclicker.server.manage.AnswerManager;
import org.softclicker.server.manage.QuestionManager;
import org.softclicker.server.manage.UserManager;
import org.softclicker.server.gui.MainApplication;

public class Bootstrap {

    private final static Logger log = LogManager.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        ScopingDataSource scopingDataSource = new ScopingDataSource(new ServerConfigManager());
        DatabaseCreator creator = new DatabaseCreator(scopingDataSource);
        creator.createDbStructureIfNotExists();
        //read all users
        log.info("-----------------Users-------------------");
        UserManager userManager = new UserManager(scopingDataSource);
        for (User user : userManager.getAllUsers()) {
            log.info(user);
        }
        //read all questions
        log.info("---------------Questions-----------------");
        QuestionManager questionManager = new QuestionManager(scopingDataSource);
        for (Question question : questionManager.getAllQuestions()) {
            log.info(question);
        }
        //read all answers
        log.info("----------------Answers------------------");
        AnswerManager answerManager = new AnswerManager(scopingDataSource);
        for (Answer answer : answerManager.getAllAnswers()) {
            log.info(answer);
        }
//        questionManager.getQuestionsByClass("NETWORKING");
        log.info("SoftClicker Server Started!");
        // Start UI application
        MainApplication app = MainApplication.getInstance();
        app.setAnswerManager(answerManager);
        app.setQuestionManager(questionManager);
        app.setUserManager(userManager);
        app.main(args);
        log.info("SoftClicker UI Finished!");
    }
}
