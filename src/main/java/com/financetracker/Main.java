package com.financetracker;

import com.financetracker.ui.DashboardUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        DashboardUI dashboard = new DashboardUI();
        Scene scene = new Scene(dashboard.getRoot(), 900, 500);
        primaryStage.setTitle("Finance Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
