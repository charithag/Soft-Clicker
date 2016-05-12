package org.softclicker.server.gui.controllers;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.scene.Node;
import org.softclicker.server.gui.controllers.quiz.QuestionController;

import javax.annotation.PostConstruct;

/**
 * Created by chamika on 5/6/16.
 */
public class ParentController {

    @FXMLViewFlowContext
    protected ViewFlowContext context;

    protected FlowHandler contentFlowHandler;
    protected Flow contentFlow;

    @PostConstruct
    public void init() {
        contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
        contentFlow = (Flow) context.getRegisteredObject("ContentFlow");
    }

    protected void bindNodeToController(Node node, Class<?> controllerClass, Flow flow, FlowHandler flowHandler) {
        flow.withGlobalLink(node.getId(), controllerClass);
        node.setOnMouseClicked((e) -> {
            try {
                onDestroy(flowHandler);
                flowHandler.handle(node.getId());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    protected void onDestroy(FlowHandler flowHandler) {
        try {
            Object controller = flowHandler.getCurrentView().getViewContext().getController();
            if (controller instanceof QuestionController) {
                ((QuestionController) controller).stopNetworks();
            }
        } catch (NullPointerException e) {
            //skip
        }
    }
}
