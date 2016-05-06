package org.softclicker.server.gui.controllers.quiz;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.FXMLController;
import javafx.fxml.FXML;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;
import org.softclicker.server.dao.impl.QuestionDAO;
import org.softclicker.server.entity.Clazz;
import org.softclicker.server.entity.Question;
import org.softclicker.server.gui.MainApplication;
import org.softclicker.server.gui.controllers.ParentController;
import org.softclicker.server.gui.controllers.history.HistoryController;

import javax.annotation.PostConstruct;

/**
 * Created by chamika on 5/6/16.
 */
@FXMLController(value = "/fxml/ui/NewQuiz.fxml", title = "New Quiz")
public class NewQuizController extends ParentController {

    private final static Logger log = LogManager.getLogger(NewQuizController.class);

    @FXML
    private JFXTextField classYearText;
    @FXML
    private JFXTextField classNameText;
    @FXML
    private JFXButton submitButton;

    @PostConstruct
    public void init() {
        super.init();

        contentFlow.withGlobalLink(submitButton.getId(), QuestionController.class);

        submitButton.setOnAction(event -> {
            try {

                Clazz clazz = new Clazz(classNameText.getText(), Integer.parseInt(classYearText.getText()));
                MainApplication.getInstance().getClazzManager().saveClazz(clazz);
                context.register("class", clazz);
                contentFlowHandler.handle(submitButton.getId());
            } catch (Exception e) {
                log.error("Error submitting data", e);
            }
        });

    }
}
