package com.medisys;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class AppointmentOne_2_Ctrler {
	private Stage stage;
	private Scene scene;
	private Parent root;

    @FXML
    public void initialize() {
    	
    }
    
    public void switchToScene1(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("/com/medisys/AppointmentOne_1.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void switchToScene2(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("/com/medisys/AppointmentOne_2.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
}
