package org.softclicker.client.gui.controllers;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.softclicker.client.gui.MainWindow;

import javax.annotation.PostConstruct;

/**
 * Created by Admin on 5/6/2016.
 *
 */
@FXMLController(value = "/fxml/Main.fxml", title = "Soft Clicker")
public class MainController {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button openingButton;

    @FXML
    private TextField idInputText;

    @FXML
    private Label idInputLable;

    @PostConstruct
    public void init() throws FlowException, VetoException {

        openingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {

                MainWindow mainWindow = MainWindow.getInstance();
                try
                {
                    AnswerPane answerPane = new AnswerPane( idInputText != null ? idInputText.getText() : "" );

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        DropShadow shadow = new DropShadow();
//Adding the shadow when the mouse cursor is on
        openingButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        openingButton.setEffect(shadow);
                    }
                });
//Removing the shadow when the mouse cursor is off
        openingButton.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        openingButton.setEffect(null);
                    }
                });
    }
}
