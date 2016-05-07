package org.softclicker.server.gui.controllers.quiz;

import com.jfoenix.controls.JFXButton;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.action.BackAction;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.entity.Clazz;
import org.softclicker.server.gui.controllers.ParentController;

import javax.annotation.PostConstruct;

/**
 * Created by chamika on 5/6/16.
 */
@FXMLController(value = "/fxml/ui/NewQuestion.fxml", title = "New Question")
@Deprecated
public class NewQuestionController extends ParentController {

    private final static Logger log = LogManager.getLogger(NewQuestionController.class);

    @FXML
    JFXButton saveButton;

    @FXML
    JFXButton backButton;

    @PostConstruct
    public void init() {
        super.init();
        if (((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
            Platform.runLater(() -> ((Pane) ((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));

        backButton.setOnAction(event -> goBack());
        saveButton.setOnAction(event -> save());

        Clazz clazz = (Clazz) context.getRegisteredObject("class");
        log.debug(clazz.toString());
    }

    private void goBack() {
        try {
            contentFlowHandler.handle("newQuizBack");
        } catch (VetoException | FlowException e) {
            log.error("Unable to go back", e);
        }
    }

    private void save(){

    }

}
