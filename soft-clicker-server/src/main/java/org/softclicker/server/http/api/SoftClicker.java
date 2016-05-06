package org.softclicker.server.http.api;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.config.ServerConfigManager;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.Answer;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.manage.AnswerManager;
import org.softclicker.server.manage.QuestionManager;
import org.softclicker.server.manage.UserManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/softclicker")
public class SoftClicker {

    private final static Logger log = LogManager.getLogger(SoftClicker.class);

    @GET
    @Path("/questions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Question> getAllQuestions() {
        ScopingDataSource scopingDataSource = new ScopingDataSource(new ServerConfigManager());
        QuestionManager questionManager = new QuestionManager(scopingDataSource);
        try {
            return questionManager.getAllQuestions();
        } catch (SoftClickerException e) {
            throw new WebApplicationException("Server error occurred!");
        }
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        ScopingDataSource scopingDataSource = new ScopingDataSource(new ServerConfigManager());
        UserManager userManager = new UserManager(scopingDataSource);
        try {
            return userManager.getAllUsers();
        } catch (SoftClickerException e) {
            throw new WebApplicationException("Server error occurred!");
        }
    }

    @GET
    @Path("/answers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Answer> getAllAnswers() {
        ScopingDataSource scopingDataSource = new ScopingDataSource(new ServerConfigManager());
        AnswerManager answerManager = new AnswerManager(scopingDataSource);
        try {
            return answerManager.getAllAnswers();
        } catch (SoftClickerException e) {
            throw new WebApplicationException("Server error occurred!");
        }
    }
}
