package com.appointment;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class AppointmentUIMain extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        //load fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Appointment.fxml"));
        Parent root = loader.load();
        
        //controller @@
        AppointmentController controller = new AppointmentController();
        loader.setController(controller);
        
        //cái này copy từ bên cms qua :)))
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("My JavaFX App");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}