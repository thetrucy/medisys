package com.medisys;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medisys/view/Login.fxml"));
        double preferredWidth = 1280;
        double preferredHeight = 720;
        Scene scene = new Scene(loader.load(), preferredWidth, preferredHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Medisys");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}