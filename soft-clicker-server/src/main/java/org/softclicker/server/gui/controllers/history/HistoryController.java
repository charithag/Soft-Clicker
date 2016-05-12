package org.softclicker.server.gui.controllers.history;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.entity.Answer;
import org.softclicker.server.entity.Clazz;
import org.softclicker.server.entity.Question;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.gui.MainApplication;
import org.softclicker.server.gui.components.AnswerChart;
import org.softclicker.server.gui.controllers.ParentController;
import org.softclicker.server.manage.AnswerManager;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chamika on 5/1/16.
 */
@FXMLController(value = "/fxml/ui/History.fxml", title = "History")
public class HistoryController extends ParentController{

    private final static Logger log = LogManager.getLogger(HistoryController.class);

    @FXML
    private Pane chartPane;

    @FXML
    private JFXComboBox classesCombo;

    @FXML
    private JFXListView questionsList;

    private AnswerChart chart;

    private List<Clazz> validClasses;
    private List<Question> questionsByClass;

    @PostConstruct
    public void init() {
        super.init();
        if (((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
            Platform.runLater(() -> ((Pane) ((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));

        classesCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int selectedIndex = newValue.intValue();
            if (validClasses.size() > selectedIndex) {
                loadQuestions(validClasses.get(selectedIndex).getId());
            }
        });

        questionsList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int selectedIndex = newValue.intValue();
            if (questionsByClass != null && questionsByClass.size() > selectedIndex) {
                loadAnswers(questionsByClass.get(selectedIndex).getQuestionId());
            }
        });

        chart = new AnswerChart();
        chartPane.getChildren().add(chart);

        // set values to classes combo box
        loadClasses();
    }

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

    /**
     * Load and show questions for the class name
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

    /**
     * Load the classes which have questions
     */
    private void loadClasses() {
        resetQuestionsList();
        validClasses = MainApplication.getInstance().getQuestionManager().getValidClasses();
        if (validClasses != null && !validClasses.isEmpty()) {
            ObservableList<String> items = FXCollections.observableArrayList(
                    validClasses.stream().map(c -> new String(c.getName() + " " + c.getYear())).collect(Collectors.toList()));
            classesCombo.setItems(items);
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

}
