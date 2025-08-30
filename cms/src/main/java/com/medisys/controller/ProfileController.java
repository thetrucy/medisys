package com.medisys.controller;

import com.medisys.util.DatabaseManager;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.medisys.model.Patient;
import com.medisys.model.User;
import com.medisys.util.CurrentUser;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;

import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class ProfileController {
    private Patient currentPatient;
    private DatabaseManager dbManager;

    @FXML
    private TextField IDField, nameField, phoneField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private CheckBox femaleCheckbox, maleCheckbox;

    @FXML
    private Button profileEditButton, profileSaveButton;

    @FXML
    private String selectedGender = "";
    
    // Phương thức này được JavaFX tự động gọi sau khi load FXML xong
    @FXML
    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        User currentUser = CurrentUser.getInstance().getCurrentUser();
        
        if (currentUser instanceof Patient) {
            this.currentPatient = (Patient) currentUser;
            populatePatientData();
        }
        setFieldsDisabled(true);
    }

    private void populatePatientData() {
        IDField.setText(currentPatient.getId());
        nameField.setText(currentPatient.getName());
        phoneField.setText(currentPatient.getPhone());

        if (currentPatient.getDOB() != null && !currentPatient.getDOB().isEmpty()) {
            try {
                LocalDate dob = LocalDate.parse(currentPatient.getDOB());
                dobPicker.setValue(dob);
            } catch (DateTimeParseException e) {
                dobPicker.setValue(null);
            }
        }

        selectedGender = currentPatient.getGender();
        if ("Nam".equalsIgnoreCase(selectedGender)) {
            maleCheckbox.setSelected(true);
            femaleCheckbox.setSelected(false);
        } else if ("Nữ".equalsIgnoreCase(selectedGender)) {
            femaleCheckbox.setSelected(true);
            maleCheckbox.setSelected(false);
        }
    }

    private void setFieldsDisabled(boolean disabled) { // Không cho sửa ID
        nameField.setDisable(disabled);
        phoneField.setDisable(disabled);
        dobPicker.setDisable(disabled);
        femaleCheckbox.setDisable(disabled);
        maleCheckbox.setDisable(disabled);
        profileEditButton.setVisible(disabled);
        profileSaveButton.setVisible(!disabled);
    }

    @FXML
    void chooseFemaleGender(ActionEvent event) {
        if (femaleCheckbox.isSelected()) {
            maleCheckbox.setSelected(false); // bỏ chọn Nam nếu chọn Nữ
            selectedGender = "Nữ";
        } else {
            selectedGender = ""; // bỏ chọn Nữ thì không có giới tính nào
        }
    }

    @FXML
    void chooseMaleGender(ActionEvent event) {
        if (maleCheckbox.isSelected()) {
            femaleCheckbox.setSelected(false); // bỏ chọn Nữ nếu chọn Nam
            selectedGender = "Nam";
        } else {
            selectedGender = "";
        }
    }

    @FXML
    private void handleProfileEditChanges(javafx.event.ActionEvent event) {
        Button clicked = (Button) event.getSource();

    	if (clicked == profileEditButton) {
	        animatePop(profileEditButton);
	    }
        // Hiển thị hộp thoại xác nhận
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn thay đổi thông tin?", ButtonType.OK, ButtonType.CANCEL);
        confirmation.setTitle("Xác nhận");
        confirmation.setHeaderText(null);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                setFieldsDisabled(false);
            }
        });
    }

    @FXML
    void handleProfileSaveChanges(javafx.event.ActionEvent event) {
        Button clicked = (Button) event.getSource();
        if (clicked == profileSaveButton) {
            animatePop(profileSaveButton);
        }

        String newName = nameField.getText();
        String newPhone = phoneField.getText().trim();
        String newDob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : "";

        // --- Name validation ---
        if (newName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Họ tên không được để trống.");
            return;
        }
        if (!isValidName(newName)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Họ và tên không hợp lệ. Vui lòng chỉ sử dụng chữ cái và khoảng trắng.");
            return;
        }
        
        // --- Phone validation ---
        if (!newPhone.matches("^0\\d{9}$")) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Số điện thoại không hợp lệ. Phải bắt đầu bằng 0 và có 10 chữ số.");
            return;
        }

        if (newName.equals(currentPatient.getName()) && 
            newPhone.equals(currentPatient.getPhone()) && 
            newDob.equals(currentPatient.getDOB()) &&
            selectedGender.equals(currentPatient.getGender())) {
            
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Bạn chưa thay đổi thông tin nào.");
            setFieldsDisabled(true);
            return;
        }
        
        currentPatient.setName(newName);
        currentPatient.setPhone(newPhone);
        currentPatient.setDOB(newDob);
        currentPatient.setGender(selectedGender);

        boolean success = dbManager.updatePatient(currentPatient);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu thay đổi thành công!");
            System.out.println("Họ tên mới: " + newName);
            System.out.println("SDT mới: " + newPhone);
            System.out.println("Ngày sinh mới: " + newDob);
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu thay đổi. Vui lòng thử lại.");
        }
        setFieldsDisabled(true);
    }

    @FXML
    public void setPatient(Patient patient) {
        this.currentPatient = patient;

        // Fill UI fields
        IDField.setText(String.valueOf(patient.getId()));
        nameField.setText(patient.getName());
        phoneField.setText(patient.getPhone());

        // Nếu có ngày sinh/gender thì gán vào đây nữa
        // emailField.setText(...);
        if (patient.getDOB() != null && !patient.getDOB().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dobLocalDate = LocalDate.parse(patient.getDOB(), formatter);

            dobPicker.setValue(dobLocalDate);
        }
        else {
            dobPicker.setValue(null); // Nếu không có ngày sinh thì để trống
        }
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidName(String fullName) {
    // Regex này cho phép chữ cái (bao gồm cả tiếng Việt có dấu), khoảng trắng, và các ký tự .'-
    return fullName.matches("^[\\p{L} .'-]+$");
}
}