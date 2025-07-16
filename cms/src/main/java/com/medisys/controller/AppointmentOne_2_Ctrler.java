package com.medisys.controller;

import java.io.IOException;

import com.medisys.Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class AppointmentOne_2_Ctrler {
	private Stage stage;
	private Scene scene;
	private Parent root;

	@FXML private Button homeButton;
    @FXML private Button appointmentsButton;
    @FXML private Button profileButton;

    @FXML
    public void initialize() {
    	
    }
    
    public void switchToScene1(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("/com/medisys/view/AppointmentOne_1.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void switchToScene2(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("/com/medisys/view/AppointmentOne_2.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	@FXML
    private void onHomeButtonClick(ActionEvent event) {
        try {
            Main.setRoot("AppointmentOne_1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAppointmentsButtonClick(ActionEvent event) {
        try {
            Main.setRoot("Appointment");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onProfileButtonClick(ActionEvent event) {
        try {
            Main.setRoot("PatientProfile");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }	
}