package org.softclicker.server.gui.controllers.home;

import com.jfoenix.controls.JFXButton;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.gui.controllers.ParentController;

import javax.annotation.PostConstruct;

/**
 * Created by chamika on 5/6/16.
 */
@FXMLController(value = "/fxml/ui/Home.fxml", title = "Home")
public class HomeController extends ParentController{
    private final static Logger log = LogManager.getLogger(HomeController.class);

    @FXML
    JFXButton historyButton;

    @FXML
    JFXButton newQuizButton;

    @PostConstruct
    public void init() {
        if (((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
            Platform.runLater(() -> ((Pane) ((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));
        
        historyButton.setOnAction(event -> {});
        newQuizButton.setOnAction(event -> {});
    }

}
