package com.medisys.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.medisys.Main;
import com.medisys.model.UpcomingAppointment;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

public class UpcomingApmController implements Initializable {
    @FXML
    private TableView<UpcomingAppointment> appointmentTable;

    @FXML
    private TableColumn<UpcomingAppointment, String> dateColumn;

    @FXML
    private TableColumn<UpcomingAppointment, String> roomColumn;

    @FXML
    private TableColumn<UpcomingAppointment, String> departmentColumn;

    @FXML
    private TableColumn<UpcomingAppointment, String> doctorColumn;

    @FXML
    private TableColumn<UpcomingAppointment, String> notesColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Gán column -> field trong class Appointment
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        // Dữ liệu mẫu
        ObservableList<UpcomingAppointment> sampleAppointments = FXCollections.observableArrayList(
            new UpcomingAppointment("2025-07-01", "1", "Tai Mũi Họng", "BS. Trần Văn A", "Khám định kỳ"),
            new UpcomingAppointment("2025-07-10", "2", "Da Liễu", "BS. Nguyễn Thị B", "Theo dõi mụn trứng cá")
        );

        appointmentTable.setItems(sampleAppointments);

        appointmentTable.setOnMouseClicked(event -> {
            UpcomingAppointment selected = appointmentTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                System.out.println("Đã chọn lịch khám:");
                System.out.println("Ngày: " + selected.getDate());
                System.out.println("Khoa: " + selected.getDepartment());
                System.out.println("Bác sĩ: " + selected.getDoctor());
                System.out.println("Ghi chú: " + selected.getNotes());
            }
        });
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
            Main.setRoot("UpcomingAppointments"); 
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