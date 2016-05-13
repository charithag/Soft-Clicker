package org.softclicker.client.gui.controllers;

/**
 * Created by Admin on 5/6/2016.
 */

import com.jfoenix.controls.JFXDecorator;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.softclicker.client.gui.MainWindow;
import org.softclicker.client.transport.TransportManager;
import org.softclicker.message.dto.SoftClickAnswer;

import javax.annotation.PostConstruct;

@FXMLController(value = "/fxml/answerPane.fxml", title = "Soft Clicker")
public class AnswerPane {

    @FXML
    private Pane answerPane;

    @FXML
    private ToolBar idToolBar;

    @FXML
    private Label idLable;

    @FXML
    private Label idoOutputLable;

    @FXML
    private Button changeIdButton;

    @FXML
    private ToolBar submitToolBar;

    @FXML
    private Button resetButton;

    @FXML
    private Button submitButton;

    @FXML
    private RadioButton radioButton1;

    @FXML
    private RadioButton radioButton2;

    @FXML
    private RadioButton radioButton3;

    @FXML
    private RadioButton radioButton4;

    @FXML
    private Label questionNoLable;

    private String studentId;
    private SoftClickAnswer.AnswerOption answerOption;

    private FlowHandler flowHandler;
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @PostConstruct
    public void init() throws FlowException, VetoException {

        Stage stage = MainWindow.primaryStage;
        studentId = (String)context.getRegisteredObject("sid");
        idoOutputLable.setText(studentId);
        final ToggleGroup group = new ToggleGroup();
        radioButton1.setToggleGroup(group);
        radioButton2.setToggleGroup(group);
        radioButton3.setToggleGroup(group);
        radioButton4.setToggleGroup(group);

        //This is for changing the student id in the answer panel
        changeIdButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                try {

                    Stage stage = new Stage();
                    Flow flow = new Flow(MainController.class);
                    DefaultFlowContainer container = new DefaultFlowContainer();
                    context = new ViewFlowContext();
                    context.register("Stage", stage);

                    flow.createHandler(context).start(container);

                    Scene scene = new Scene( new JFXDecorator( stage , container.getView() ), 400, 300);
                    scene.getStylesheets().add(getClass().getResource("/css/jfoenix-fonts.css").toExternalForm());
                    scene.getStylesheets().add(getClass().getResource("/css/jfoenix-design.css").toExternalForm());
                    scene.getStylesheets().add(getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm());
                    stage.setScene( scene );

                    stage.show();

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });


        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (radioButton1.isSelected()) {
                    answerOption = SoftClickAnswer.AnswerOption.OPTION_1;
                } else if (radioButton2.isSelected()) {
                    answerOption = SoftClickAnswer.AnswerOption.OPTION_2;
                } else if (radioButton3.isSelected()) {
                    answerOption = SoftClickAnswer.AnswerOption.OPTION_3;
                } else {
                    answerOption = SoftClickAnswer.AnswerOption.OPTION_4;
                }

                TransportManager manager = TransportManager.getInstance();
                if (studentId == null) {
                    studentId = "Anonymus";
                    manager.sendAnswers(studentId, answerOption);
                }else{
                    manager.sendAnswers(studentId, answerOption);
                }
            }

        });

        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                radioButton1.setSelected(false);
                radioButton2.setSelected(false);
                radioButton3.setSelected(false);
                radioButton4.setSelected(false);
            }

        });
    }

    public Pane getAnswerPane() {
        if (this.answerPane == null) {
            this.answerPane = new Pane();
        }
        return answerPane;
    }

    public void initData(String studentId) {
        this.studentId = studentId;
    }
}

