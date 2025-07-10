package com.medisys;

import com.medisys.util.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL; // Import URL class

public class Main extends Application {

    private static Scene scene;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseManager.getInstance();

        String initialFxmlPath = "view/AppointmentOne_1"; 
        System.out.println("Main: Attempting to load initial FXML: " + initialFxmlPath);

        Parent initialRoot = loadFXML(initialFxmlPath);
        scene = new Scene(initialRoot);
        stage.setScene(scene);
        stage.setTitle("MediSys CMS");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        System.out.println("Main: Attempting to set root to FXML: view/" + fxml);

        scene.setRoot(loadFXML("view/" + fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
 
        URL fxmlLocation = Main.class.getResource(fxml + ".fxml");


        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        return fxmlLoader.load();
    }
}