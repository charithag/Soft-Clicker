package org.softclicker.client.gui.controllers;

/**
 * Created by Admin on 5/6/2016.
 */

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.softclicker.client.gui.MainWindow;

import javax.annotation.PostConstruct;

@FXMLController(value = "/fxml/answerPane.fxml", title = "Soft Clicker")
public class AnswerPane {

    @FXML
    private AnchorPane answerPane;

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

    private String studentId;

    @PostConstruct
    public void init() throws FlowException, VetoException {

        Stage stage  = MainWindow.primaryStage;
        idoOutputLable.setText( this.studentId );
    }

    public AnchorPane getAnswerPane() {
        if (this.answerPane == null)
        {
            this.answerPane = new AnchorPane();
        }
        return answerPane;
    }

    public void initData(String studentId)
    {this.studentId = studentId;}
}

