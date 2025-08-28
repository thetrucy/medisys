package com.medisys.controller;

import com.medisys.model.Doctor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private StackPane contentArea;

    public void initialize() throws Exception {
        loadView("AppointmentFirst.fxml"); // Load view mặc định
    }

    public void loadAppointmentFirstView() throws Exception {
        loadView("AppointmentFirst.fxml");
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

    public void loadProfileView() throws Exception {
        loadView("PatientProfile.fxml");
    }

    public void loadUpcomingApmView() throws Exception {
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

        // The check for BookApmController is no longer needed here,
        // as it's handled by the new loadBookingView(Doctor doctor) method.

        contentArea.getChildren().setAll(node);
    }
};