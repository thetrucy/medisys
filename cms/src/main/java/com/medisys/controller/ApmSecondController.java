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

public class ApmSecondController {
    // ====== ADDED ======
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    // ===================
	@FXML private Button homeButton;
    @FXML private Button appointmentsButton;
    @FXML private Button profileButton;

    @FXML
    public void initialize() {
    	
    }
    
    public void switchToScene1(ActionEvent event) throws IOException {
        try {
            mainController.loadAppointmentFirstView();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void switchToScene2(ActionEvent event) throws IOException {
        try {
            mainController.loadAppointmentSecondView();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}	
}