package org.softclicker.server.gui.controllers.connection;

import com.jfoenix.controls.JFXButton;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;

@FXMLController(value = "/fxml/ui/Discovery.fxml", title = "Soft Clicker")
@Deprecated
public class DiscoveryController {

    private final static Logger log = LogManager.getLogger(DiscoveryController.class);

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private JFXButton buttonDiscovery;

    @PostConstruct
    public void init() throws FlowException, VetoException {
        if (((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
            Platform.runLater(() -> ((Pane) ((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));


        buttonDiscovery.setOnAction(event -> startDiscovery());
    }

    private void startDiscovery() {
        log.info("starting Discovery");

    }

}
