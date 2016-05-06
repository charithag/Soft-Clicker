package org.softclicker.client.gui;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyphLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.client.gui.controllers.AnswerPane;
import org.softclicker.client.gui.controllers.MainController;

/**
 * Created by Admin on 5/6/2016.
 */
public class MainWindow extends Application
{

    private final static Logger log = LogManager.getLogger(MainWindow.class);
    private static MainWindow instance;
    public static Stage primaryStage;


    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    public static MainWindow getInstance() {
        if (instance == null) {
            instance = new MainWindow();
        }
        return instance;
    }

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

        primaryStage = stage;
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
