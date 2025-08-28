package com.medisys;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.stage.Stage;



public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medisys/view/Login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Medisys");
        primaryStage.show();
    }

    public static void main(String[] args) {
        // THÊM DÒNG NÀY VÀO ĐỂ KIỂM TRA
        com.medisys.util.DatabaseManager.getInstance();
        launch(args);
    }
}