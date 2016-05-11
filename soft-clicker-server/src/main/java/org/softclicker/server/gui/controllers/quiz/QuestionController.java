package org.softclicker.server.gui.controllers.quiz;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import io.datafx.controller.FXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.entity.Answer;
import org.softclicker.server.entity.Clazz;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.gui.MainApplication;
import org.softclicker.server.gui.components.AnswerChart;
import org.softclicker.server.gui.controllers.ParentController;
import org.softclicker.server.manage.AnswerManager;
import org.softclicker.server.handler.ServerHandler;
import org.softclicker.server.handler.ServerHandlerFactory;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chamika on 5/6/16.
 */
@FXMLController(value = "/fxml/ui/Question.fxml", title = "Questions")
public class QuestionController extends ParentController implements AnswerListener {

    private final static Logger log = LogManager.getLogger(QuestionController.class);

    @FXML
    private Pane chartPane;

    @FXML
    private JFXListView questionsList;

    @FXML
    private Label classNameLabel;

    @FXML
    private JFXButton newQuestionButton;

    @FXML
    TextField newQuestionText;

    private AnswerChart chart;

    private List<Clazz> validClasses;
    private List<Question> questionsByClass;

    private Clazz clazz;
    private User user;
    private ServerHandler broadCastingServer;

    @PostConstruct
    public void init() {
        super.init();
        if (((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
            Platform.runLater(() -> ((Pane) ((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));

        chart = new AnswerChart();
        chartPane.getChildren().add(chart);

        questionsList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int selectedIndex = newValue.intValue();
            if (questionsByClass.size() > selectedIndex) {
                Question question = questionsByClass.get(selectedIndex);
                loadAnswers(question.getQuestionId());
                startListeningAnswers(question);
            }
        });

        newQuestionButton.setOnAction(event -> {
            saveQuestion(newQuestionText.getText(), clazz.getId());
        });

        user = MainApplication.getInstance().getLoggedUser();
        clazz = (Clazz) context.getRegisteredObject("class");
        if (clazz != null) {
            classNameLabel.setText(clazz.getName() + " - " + clazz.getYear());
            loadQuestions(clazz.getId());
            startDiscovery(clazz);
        }

    }

    /**
     * Count answers and update graph
     *
     * @param questionId
     */
    private void loadAnswers(int questionId) {
        AnswerManager answerManager = MainApplication.getInstance().getAnswerManager();
        try {
            List<Answer> answersByQuestionId = answerManager.getAnswersByQuestionId(questionId);
            log.info("answer count=" + answersByQuestionId.size());
            HashMap<String, Integer> answersCount = new HashMap<>();
            for (Answer answer : answersByQuestionId) {
                Integer count = answersCount.get(answer.getAnswer());
                if (count == null) {
                    count = 1;
                } else {
                    ++count;
                }
                answersCount.put(answer.getAnswer(), count);
            }
            resetGraph();
            answersCount.forEach((s, integer) -> chart.updateData(s, integer));
            log.info("answers= " + answersCount);
        } catch (SoftClickerException e) {
            log.error("Unable to load answers", e);
        }
    }

    private void saveQuestion(String questionText, int classID) {
        Question question = new Question(-1, questionText, Answer.ANSWERS.A.name(), user, new Date(), new Date(), classID);
        boolean status = MainApplication.getInstance().getQuestionManager().saveQuestion(question);
        if (status) {
            loadQuestions(classID);
            log.info("question saved. " + question.toString());
        }
    }

    /**
     * Load and show questions for the class name
     *
     * @param classID
     */
    private void loadQuestions(int classID) {
        //load all questions for a class Name
        resetQuestionsList();
        try {
            questionsByClass = MainApplication.getInstance().getQuestionManager().getQuestionsByClassID(classID);
            if (questionsByClass != null && !questionsByClass.isEmpty()) {
                ObservableList<String> items = FXCollections.observableArrayList(
                        questionsByClass.stream().map(Question::getQuestion).collect(Collectors.toList()));
                questionsList.setItems(items);
            }
        } catch (SoftClickerException e) {
            log.error("Unable to questions", e);
        }

    }

    private void resetQuestionsList() {
        questionsByClass = null;
        questionsList.setItems(FXCollections.observableArrayList());
        resetGraph();
    }


    private void resetGraph() {
        chart.clear();
    }

    private void startDiscovery(Clazz clazz) {
        try {
            this.broadCastingServer = ServerHandlerFactory.createBroadcastingHandler();
        } catch (SoftClickerException e) {
            log.error("Cannot create broadcasting server", e);
        }
    }

    private void stopDiscovery() {
        this.broadCastingServer.stop();
    }

    private void startListeningAnswers(Question question) {
        try {
            ServerHandlerFactory.createListeningHandler(question, this);
        } catch (SoftClickerException e) {
            log.error("Cannot start listening server for question '" + question + "'");
        }
        log.info("Server started listening answers for question '" + question + "'");
    }

    @Override
    public void answerReceived(Answer answer) throws SoftClickerException {
        boolean status = MainApplication.getInstance().getAnswerManager().saveAnswer(answer);
        if (status) {
            loadAnswers(answer.getQuestion().getQuestionId());
        }
        throw new SoftClickerException("Error on saving answer with id '" + answer.getAnswerId() +
                "' for the question id '" + answer.getQuestion().getQuestionId());
    }
}

