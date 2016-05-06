package org.softclicker.server.gui.controllers.quiz;

import io.datafx.controller.FXMLController;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.entity.Clazz;
import org.softclicker.server.gui.controllers.ParentController;

import javax.annotation.PostConstruct;

/**
 * Created by chamika on 5/6/16.
 */
@FXMLController(value = "/fxml/ui/Questions.fxml", title = "Questions")
public class QuestionController extends ParentController{

    private final static Logger log = LogManager.getLogger(QuestionController.class);

    @PostConstruct
    public void init(){
        super.init();

        Clazz clazz = (Clazz)context.getRegisteredObject("class");
        log.debug(clazz.toString());
    }

}
