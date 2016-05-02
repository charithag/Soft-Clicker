package org.softclicker.server.gui.components;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.*;
import javafx.util.Duration;

import java.util.HashMap;

/**
 * Created by chamika on 5/1/16.
 */
public class AnswerChart extends BarChart<Number, String> {

    private final String ANSWERS = " Answers";

    private XYChart.Series<Number, String> series;
    private String questionName;
    private HashMap<String, XYChart.Data> dataMap;


    final static String ANSWER_A = "A";
    final static String ANSWER_B = "B";
    final static String ANSWER_C = "C";
    final static String ANSWER_D = "D";
    final static String ANSWER_E = "E";

    public AnswerChart() {
        super(new NumberAxis(), new CategoryAxis());
        init();
    }

    public AnswerChart(@NamedArg("xAxis") Axis axis, @NamedArg("yAxis") Axis axis2) {
        super(axis, axis2);
        init();
    }

    public AnswerChart(@NamedArg("xAxis") Axis axis, @NamedArg("yAxis") Axis axis2, @NamedArg("data") ObservableList data) {
        super(axis, axis2, data);
        init();
    }

    public AnswerChart(@NamedArg("xAxis") Axis axis, @NamedArg("yAxis") Axis axis2, @NamedArg("data") ObservableList data, @NamedArg("categoryGap") double categoryGap) {
        super(axis, axis2, data, categoryGap);
        init();
    }

    private void init() {
        getXAxis().setLabel("Count");
        getXAxis().setTickLabelRotation(90);
        getYAxis().setLabel("Answer");
        getYAxis().setAnimated(false);

        setLegendVisible(false);
        series = new XYChart.Series();
        this.getData().add(series);
        dataMap = new HashMap<>();

        int count = -1;
    }

    public void refreshGraphLayout() {
        if (questionName != null && questionName.length() > 0)
            setTitle(questionName + ANSWERS);
        else
            setTitle(ANSWERS);
    }

    @Deprecated
    //Remove after testing
    public void refreshData() {
        Timeline tl = new Timeline();
        tl.getKeyFrames().add(
                new KeyFrame(Duration.millis(500),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                for (XYChart.Data<Number, String> data : series.getData()) {
                                    data.setXValue(Math.random() * 90);
                                }
                            }
                        }
                ));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.setAutoReverse(true);
        tl.play();
    }

    public void updateData(String answer, Number value) {
        updateData(answer, value, 0);
    }

    /**
     * Update data if exist, otherwise add as new
     *
     * @param answer
     * @param value
     * @param index
     */
    public void updateData(String answer, Number value, int index) {
        Data data = this.dataMap.get(answer);
        if (data != null) {
            data.setXValue(value);
        } else {
            Data<Number, String> dataElement = new Data<>(value, answer);
            series.getData().add(index, dataElement);
            dataMap.put(answer, dataElement);
        }
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
        refreshGraphLayout();
    }
}
