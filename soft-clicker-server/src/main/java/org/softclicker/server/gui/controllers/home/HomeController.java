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
import org.softclicker.server.entity.Clazz;
import org.softclicker.server.gui.MainApplication;
import org.softclicker.server.gui.controllers.ParentController;
import org.softclicker.server.gui.controllers.connection.DiscoveryController;
import org.softclicker.server.gui.controllers.history.HistoryController;
import org.softclicker.server.gui.controllers.quiz.NewQuizController;
import org.softclicker.server.gui.controllers.quiz.QuestionController;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    @FXML JFXButton quickQuizButton;

    @PostConstruct
    public void init() {
        super.init();
        if (((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
            Platform.runLater(() -> ((Pane) ((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));

        bindNodeToController(historyButton, HistoryController.class, contentFlow, contentFlowHandler);
        bindNodeToController(newQuizButton, NewQuizController.class, contentFlow, contentFlowHandler);

        contentFlow.withGlobalLink(quickQuizButton.getId(), QuestionController.class);
        quickQuizButton.setOnMouseClicked((e) -> {
            try {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d kk:mm");
                Clazz clazz = new Clazz(-1, "Quiz-" + sdf.format(cal.getTime()) , cal.get(Calendar.YEAR));
                MainApplication.getInstance().getClazzManager().saveClazz(clazz);
                context.register("class", clazz);
                onDestroy(contentFlowHandler);
                contentFlowHandler.handle(quickQuizButton.getId());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

}
