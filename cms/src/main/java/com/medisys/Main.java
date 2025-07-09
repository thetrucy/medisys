// src/main/java/com/medisys/Main.java
package com.medisys;

import com.medisys.util.DatabaseManager; // <--- Make sure this import is present
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Scene scene;
    private DatabaseManager dbManager;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // --- Database Initialization (RE-ADDED) ---
        dbManager = new DatabaseManager(); // Initialize your DatabaseManager
        dbManager.createTables();         // This will create tables and pre-populate doctors if needed
        // --- End Database Initialization ---

        scene = new Scene(loadFXML("view/AppointmentOne_1"));
        stage.setScene(scene);
        stage.setTitle("MediSys CMS"); // Optional: good practice to set a title
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML("view/" + fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}