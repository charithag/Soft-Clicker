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
import org.softclicker.server.gui.controllers.connection.DiscoveryController;
import org.softclicker.server.gui.controllers.history.HistoryController;

import javax.annotation.PostConstruct;

@FXMLController(value = "/fxml/SideMenu.fxml", title = "Menu")
public class SideMenuController {

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML
	@ActionTrigger("newQuiz")
	private Label newQuiz;

	@FXML
	@ActionTrigger("history")
	private Label history;
	
	@FXML
	private JFXListView<?> sideList;

	@PostConstruct
	public void init() throws FlowException, VetoException {
		sideList.propagateMouseEventsToParent();
		FlowHandler contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
		Flow contentFlow = (Flow) context.getRegisteredObject("ContentFlow");
		bindNodeToController(newQuiz, DiscoveryController.class, contentFlow, contentFlowHandler);
		bindNodeToController(history, HistoryController.class, contentFlow, contentFlowHandler);
	}

	private void bindNodeToController(Node node, Class<?> controllerClass, Flow flow, FlowHandler flowHandler) {
		flow.withGlobalLink(node.getId(), controllerClass);
		node.setOnMouseClicked((e) -> {
			try {				
				flowHandler.handle(node.getId());				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
	}

}
