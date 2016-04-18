package org.softclicker.server.gui;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyphLoader;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.softclicker.server.gui.controllers.main.MainController;

/**
 * Created by chamika on 4/12/16.
 */
public class MainApplication extends Application {

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    @Override
    public void start(Stage stage) throws Exception {

        new Thread(() -> {
            try {
                //set gphyph font
                SVGGlyphLoader.loadGlyphsFont(getClass().getResourceAsStream("/font/icomoon.svg"), "icomoon.svg");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        Flow flow = new Flow(MainController.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        flow.createHandler(flowContext).start(container);

        Scene scene = new Scene(new JFXDecorator(stage, container.getView()), 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/jfoenix-fonts.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/jfoenix-design.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm());
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
