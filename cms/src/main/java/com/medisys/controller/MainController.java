package com.medisys.controller;

import com.medisys.model.Doctor;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class MainController {

    @FXML
    private StackPane contentArea;
    
    @FXML
    private Button HomeButton;
    
    @FXML
    private Button ViewButton;
    
    @FXML
    private Button UserButton;

    public void initialize() throws Exception {
        loadView("AppointmentFirst.fxml"); // Load view mặc định
    }

    public void loadAppointmentFirstView(javafx.event.ActionEvent event) throws Exception {
    	Button clicked = (Button) event.getSource();
    	
    	if (clicked == HomeButton) {
	        animatePop(HomeButton);
	    }
    	
        loadView("AppointmentFirst.fxml");
    }

    public void loadAppointmentSecondView() throws Exception {
        loadView("AppointmentSecond.fxml");
    }

    // public void loadBookingView() throws Exception {
    //     loadView("BookAppointment.fxml");
    // }

    // REMOVED the old loadBookingView() method.

    public void loadBookingView(Doctor doctor) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medisys/view/BookAppointment.fxml"));
        Node node = loader.load();

        // Get the controller for BookAppointment.fxml
        BookApmController bookApmController = loader.getController();

        // Inject the MainController and the selected Doctor
        bookApmController.setMainController(this);
        bookApmController.setSelectedDoctor(doctor);

        contentArea.getChildren().setAll(node);
    }

    public void loadProfileView(javafx.event.ActionEvent event) throws Exception {
    	Button clicked = (Button) event.getSource();
    	
    	if (clicked == UserButton) {
	        animatePop(UserButton);
	    }
    	
        loadView("PatientProfile.fxml");
    }

    public void loadUpcomingApmView(javafx.event.ActionEvent event) throws Exception {
    	Button clicked = (Button) event.getSource();
    	
    	if (clicked == ViewButton) {
	        animatePop(ViewButton);
	    }
    	
        loadView("UpcomingApm.fxml");
    }
    public void loadLoginView() throws Exception {
        loadView("Login.fxml");
    }
    private void loadView(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medisys/view/" + fxml));
        Node node = loader.load();

        // Inject this MainController vào BookApmFirstController
        Object controller = loader.getController();
        if (controller instanceof ApmFirstController) {
            ((ApmFirstController) controller).setMainController(this);
        }

        if (controller instanceof ApmSecondController) {
            ((ApmSecondController) controller).setMainController(this);
        }

        // The check for BookApmController is no longer needed here,
        // as it's handled by the new loadBookingView(Doctor doctor) method.

        contentArea.getChildren().setAll(node);
    }
    
    
    private void animatePop(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.09);
        st.setToY(1.09);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }
};