package com.appointment;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {
    ScheduleMaker Appointments;

    //doctor info
    @FXML private Label doctorLabel;

    //radio buttons and toggle group
    @FXML private RadioButton radioSelf;
    @FXML private RadioButton radioOther;
    @FXML private ToggleGroup appointmentGroup;

    //form containers
    @FXML private VBox selfBookingForm;
    @FXML private VBox otherBookingForm;

    //self booking form fields
    @FXML private TextField nameFieldSelf;
    @FXML private TextField dobFieldSelf;
    @FXML private TextField phoneFieldSelf;
    @FXML private ComboBox<String> genderBoxSelf;
    @FXML private DatePicker appointmentDateSelf;
    @FXML private ComboBox<String> appointmentTimeSelf;
    @FXML private Button submitButtonSelf;

    //other booking form fields
    @FXML private TextField patientNameField;
    @FXML private TextField patientDobField;
    @FXML private TextField patientPhoneField;
    @FXML private ComboBox<String> patientGenderBox;
    @FXML private ComboBox<String> relationshipBox;
    @FXML private TextField guardPhoneField;
    @FXML private TextField guardNameField;
    @FXML private DatePicker appointmentDateOther;
    @FXML private ComboBox<String> appointmentTimeOther;
    @FXML private Button submitButtonOther;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Reset time in combo box
        initializeTimeSlots();
        
        //default form
        showSelfBookingForm();
    }

    private void initializeTimeSlots() {
        //time slots for appointments
        String[] timeSlots = {
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30", "17:00"
        };
        
        appointmentTimeSelf.setItems(FXCollections.observableArrayList(timeSlots));
        appointmentTimeOther.setItems(FXCollections.observableArrayList(timeSlots));
    }

    public void setDoctorInfo(String doctorName, String doctorSpecialty) {
        doctorLabel.setText(doctorName);
        doctorLabel.setText('\n' + doctorSpecialty);
    }

    @FXML
    private void onRadioChange(ActionEvent event) {
        if (radioSelf.isSelected()) {
            showSelfBookingForm();
        } else if (radioOther.isSelected()) {
            showOtherBookingForm();
        }
    }

    private void showSelfBookingForm() {
        selfBookingForm.setVisible(true);
        selfBookingForm.setManaged(true);
        otherBookingForm.setVisible(false);
        otherBookingForm.setManaged(false);
    }

    private void showOtherBookingForm() {
        selfBookingForm.setVisible(false);
        selfBookingForm.setManaged(false);
        otherBookingForm.setVisible(true);
        otherBookingForm.setManaged(true);
    }

    @FXML
    private void handleSubmitSelf(ActionEvent event) {
        if (validateSelfBookingForm()) {
            String name = nameFieldSelf.getText().trim();
            String dob = dobFieldSelf.getText().trim();
            String phone = phoneFieldSelf.getText().trim();
            String gender = genderBoxSelf.getValue();
            LocalDate appointmentDate = appointmentDateSelf.getValue();
            String appointmentTime = appointmentTimeSelf.getValue();

            Patient patient = new Patient(name, dob, phone, gender);

            //turn date + time to datetime format assigned in AppointmentMain class
            LocalDateTime appointmentDateTime = appointmentDate.atTime(LocalTime.parse(appointmentTime));

            //make new appointment
            AppointmentMain appointment = new AppointmentMain(doctorLabel.getText(), appointmentDateTime, patient);
            
            //process the appointment, check and add to list
            if (Appointments.addAppointment(appointment)) {
                showSuccessAlert("Đặt lịch thành công!", 
                "Lịch hẹn của bạn đã được đặt thành công.\n" +
                "Ngày: " + appointmentDate + "\n" +
                "Giờ: " + appointmentTime);
            }            
            else {
                showErrorAlert("Đặt lịch thất bại.", "Buổi hẹn này đã được đặt trước.");
            }

            //clear form
            clearSelfForm();
        }
    }

    @FXML
    private void handleSubmitOther(ActionEvent event) {
        if (validateOtherBookingForm()) {
            String patientName = patientNameField.getText().trim();
            String patientDob = patientDobField.getText().trim();
            String patientPhone = patientPhoneField.getText().trim();
            String patientGender = patientGenderBox.getValue();
            String relationship = relationshipBox.getValue();
            String guardName = guardNameField.getText().trim();
            String guardPhone = guardPhoneField.getText().trim();
            LocalDate appointmentDate = appointmentDateOther.getValue();
            String appointmentTime = appointmentTimeOther.getValue();

            Patient patient = new Patient(patientName, patientDob, patientPhone, patientGender, relationship, guardPhone, guardName);

            //turn date + time to datetime format assigned in AppointmentMain class
            LocalDateTime appointmentDateTime = appointmentDate.atTime(LocalTime.parse(appointmentTime));

            //make new appointment
            AppointmentMain appointment = new AppointmentMain(doctorLabel.getText(), appointmentDateTime, patient);
            
            //process the appointment, check and add to list
            if (Appointments.addAppointment(appointment)) {
                showSuccessAlert("Đặt lịch thành công!", 
                "Lịch hẹn của bạn đã được đặt thành công.\n" +
                "Ngày: " + appointmentDate + "\n" +
                "Giờ: " + appointmentTime);
            }            
            else {
                showErrorAlert("Đặt lịch thất bại.", "Buổi hẹn này đã được đặt trước.");
            }

            //clear form
            clearOtherForm();
        }
    }

    private boolean validateSelfBookingForm() {
        StringBuilder errors = new StringBuilder();

        if (nameFieldSelf.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập họ và tên\n");
        }
        if (dobFieldSelf.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập ngày sinh\n");
        }
        if (phoneFieldSelf.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập số điện thoại\n");
        }
        if (genderBoxSelf.getValue() == null) {
            errors.append("- Vui lòng chọn giới tính\n");
        }
        if (appointmentDateSelf.getValue() == null) {
            errors.append("- Vui lòng chọn ngày khám\n");
        }
        if (appointmentTimeSelf.getValue() == null) {
            errors.append("- Vui lòng chọn giờ khám\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("Thông tin chưa đầy đủ", errors.toString());
            return false;
        }
        return true;
    }

    private boolean validateOtherBookingForm() {
        StringBuilder errors = new StringBuilder();

        if (patientNameField.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập họ và tên bệnh nhân\n");
        }
        if (patientDobField.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập ngày sinh bệnh nhân\n");
        }
        if (patientPhoneField.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập số điện thoại bệnh nhân\n");
        } 
            
        if (patientGenderBox.getValue() == null) {
            errors.append("- Vui lòng chọn giới tính\n");
        }
        if (relationshipBox.getValue() == null) {
            errors.append("- Vui lòng chọn mối quan hệ với bệnh nhân\n");
        }
        if (guardPhoneField.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập số điện thoại người giám hộ\n");
        }
        if (guardNameField.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập tên người giám hộ\n");
        }
        if (appointmentDateOther.getValue() == null) {
            errors.append("- Vui lòng chọn ngày khám\n");
        }
        if (appointmentTimeOther.getValue() == null) {
            errors.append("- Vui lòng chọn giờ khám\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("Thông tin chưa đầy đủ", errors.toString());
            return false;
        }
        return true;
    }

    private void clearSelfForm() {
        nameFieldSelf.clear();
        dobFieldSelf.clear();
        phoneFieldSelf.clear();
        genderBoxSelf.setValue(null);
        appointmentDateSelf.setValue(LocalDate.now());
        appointmentTimeSelf.setValue(null);
    }

    private void clearOtherForm() {
        patientNameField.clear();
        patientDobField.clear();
        patientPhoneField.clear();
        patientGenderBox.setValue(null);
        relationshipBox.setValue(null);
        guardPhoneField.clear();
        guardNameField.clear();
        appointmentDateOther.setValue(LocalDate.now());
        appointmentTimeOther.setValue(null);
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}