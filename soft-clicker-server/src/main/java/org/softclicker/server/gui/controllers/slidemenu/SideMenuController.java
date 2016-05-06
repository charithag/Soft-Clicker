package org.softclicker.server.gui.controllers.slidemenu;

import com.jfoenix.controls.JFXListView;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.softclicker.server.gui.controllers.ParentController;
import org.softclicker.server.gui.controllers.connection.DiscoveryController;
import org.softclicker.server.gui.controllers.history.HistoryController;
import org.softclicker.server.gui.controllers.home.HomeController;
import org.softclicker.server.gui.controllers.quiz.NewQuizController;

import javax.annotation.PostConstruct;

@FXMLController(value = "/fxml/SideMenu.fxml", title = "Menu")
public class SideMenuController extends ParentController {

	@FXML
	@ActionTrigger("home")
	private Label home;

	@FXML
	@ActionTrigger("newQuiz")
	private Label newQuiz;

	@FXML
	@ActionTrigger("history")
	private Label history;
	
	@FXML
	private JFXListView<?> sideList;

	@PostConstruct
	public void init() {
		super.init();
		sideList.propagateMouseEventsToParent();
		bindNodeToController(home, HomeController.class, contentFlow, contentFlowHandler);
		bindNodeToController(newQuiz, NewQuizController.class, contentFlow, contentFlowHandler);
		bindNodeToController(history, HistoryController.class, contentFlow, contentFlowHandler);
	}

}
