package org.softclicker.server.gui.controllers.quiz;

import io.datafx.controller.FXMLController;
import org.softclicker.server.gui.controllers.ParentController;

import javax.annotation.PostConstruct;

/**
 * Created by chamika on 5/6/16.
 */
@FXMLController(value = "/fxml/ui/Questions.fxml", title = "Questions")
public class QuestionController extends ParentController{

    @PostConstruct
    public void init(){
        super.init();

    }

}
