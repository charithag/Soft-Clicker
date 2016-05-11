package org.softclicker.client.gui.controllers;

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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.softclicker.client.gui.MainWindow;

import javax.annotation.PostConstruct;

/**
 * Created by Admin on 5/6/2016.
 */
@FXMLController(value = "/fxml/Main.fxml", title = "Soft Clicker")
public class MainController {

    @FXML
    private Pane mainPane;

    @FXML
    private Button openingButton;

    @FXML
    private TextField idInputText;

    @FXML
    private Label idInputLable;

    private FlowHandler flowHandler;
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @PostConstruct
    public void init() throws FlowException, VetoException {

        openingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                try {

                    Stage stage = new Stage();
                    Flow flow = new Flow(AnswerPane.class);
                    DefaultFlowContainer container = new DefaultFlowContainer();
                    context = new ViewFlowContext();
                    context.register("Stage", stage);

                    //registering student id for answer pane
                    context.register("sid", idInputText.getText());
                    flow.createHandler(context).start(container);

                    Scene scene = new Scene( new JFXDecorator( stage , container.getView() ), 800, 600);
                    scene.getStylesheets().add(getClass().getResource("/css/jfoenix-fonts.css").toExternalForm());
                    scene.getStylesheets().add(getClass().getResource("/css/jfoenix-design.css").toExternalForm());
                    scene.getStylesheets().add(getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm());
                    stage.setScene( scene );

                    MainWindow.primaryStage.hide();
                    stage.show();

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        DropShadow shadow = new DropShadow();
//Adding the shadow when the mouse cursor is on
        openingButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        openingButton.setEffect(shadow);
                    }
                });
//Removing the shadow when the mouse cursor is off
        openingButton.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        openingButton.setEffect(null);
                    }
                });
    }
}
