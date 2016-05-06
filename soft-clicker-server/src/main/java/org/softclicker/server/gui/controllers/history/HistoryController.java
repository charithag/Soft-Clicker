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
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.gui.MainApplication;
import org.softclicker.server.gui.components.AnswerChart;
import org.softclicker.server.manage.AnswerManager;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chamika on 5/1/16.
 */
@FXMLController(value = "/fxml/ui/History.fxml", title = "Menu")
public class HistoryController {

    private final static Logger log = LogManager.getLogger(HistoryController.class);

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private Pane chartPane;

    @FXML
    private JFXComboBox classesCombo;

    @FXML
    private JFXListView questionsList;

    private AnswerChart chart;

    @PostConstruct
    public void init() {
        if (((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
            Platform.runLater(() -> ((Pane) ((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));

        chart = new AnswerChart();
        chartPane.getChildren().add(chart);
        chart.updateData("A",14);
        chart.updateData("B",0);
        chart.updateData("C",10);
        chart.updateData("E",15);
        chart.updateData("A",20);

        //TODO pass correct values
        loadClasses();
        loadQuestions(1);
//        loadAnswers(1);
    }

    private void loadAnswers(int questionId) {
        AnswerManager answerManager = MainApplication.getInstance().getAnswerManager();
        List<Answer> answersByQuestionId = null;
        try {
            answersByQuestionId = answerManager.getAnswersByQuestionId(questionId);
        } catch (SoftClickerException e) {
            //TODO: handle this error on ui
            log.error("Error occurred retrieving question with id '" + questionId + "'", e);
        }
        HashMap<String,Integer> answersCount = new HashMap<>();
        for (Answer answer : answersByQuestionId) {
            Integer count = answersCount.get(answer.getAnswer());
            if(count == null){
                answersCount.put(answer.getAnswer(),1);
            }else {
                ++count;
            }
        }
        answersCount.forEach((s, integer) -> chart.updateData(s,integer));
    }

    private void loadQuestions(int classId)
    {
        //TODO load all questions for a class ID (after DB change)
        ObservableList<String> items = FXCollections.observableArrayList (
                "Question 1", "Question 2", "Question 3", "Question 4");
        questionsList.setItems(items);
    }

    private void loadClasses()
    {
        //TODO load all classes
        ObservableList<String> items = FXCollections.observableArrayList (
                "WAN 2016", "WAN 2015", "AOS 2016");
        classesCombo.setItems(items);
    }

}
