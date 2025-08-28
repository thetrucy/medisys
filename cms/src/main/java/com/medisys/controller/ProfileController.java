package com.medisys.controller;

import com.medisys.util.DatabaseManager;

import java.io.IOException;

import com.medisys.Main;
import com.medisys.model.Patient;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class ProfileController {
    private Patient currentPatient;
    private DatabaseManager dbManager;

    public void setDatabaseManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @FXML
    private TextField IDField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private TextField emailField;

    @FXML
    private TextField nameField;

    @FXML
    private CheckBox femaleCheckbox;

    @FXML
    private CheckBox maleCheckbox;

    @FXML
    private TextField phoneField;

    @FXML
    private Button profileEditButton;

    @FXML
    private Button profileSaveButton;

    @FXML
    private String selectedGender = "";
    
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
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác nhận");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Bạn có chắc chắn muốn thay đổi thông tin?");

        // Nếu người dùng bấm OK thì mới cho chỉnh sửa
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Cho phép chỉnh sửa (ko cho chỉnh sửa ID)
                nameField.setDisable(false);
                // emailField.setDisable(false);
                femaleCheckbox.setDisable(false);
                maleCheckbox.setDisable(false);
                phoneField.setDisable(false);
                // dobPicker.setDisable(false);

                profileEditButton.setVisible(false);
                profileSaveButton.setVisible(true);
            }
        });
    }

    @FXML
    void handleProfileSaveChanges(javafx.event.ActionEvent event) {
    	Button clicked = (Button) event.getSource();
    	
    	if (clicked == profileSaveButton) {
	        animatePop(profileSaveButton);
	    }
    	
        String name = nameField.getText();
        String phone = phoneField.getText();
        // String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : "";
        // String email = emailField.getText();

<<<<<<< Updated upstream
        // Cập nhật đối tượng hiện tại
        currentPatient.setName(name);
        currentPatient.setPhone(phone);

        // Gọi updatePatient từ DatabaseManager
=======
        // 1. Get the current values from the UI components
        String newName = nameField.getText();
        String newPhone = phoneField.getText().trim();
        String newDob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : "";

        // --- Phone validation ---
        if (!newPhone.matches("\\d{10,11}")) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Số điện thoại phải gồm 10 hoặc 11 chữ số.");
            return;
        }

        // 2. Check for changes BEFORE updating the object
        if (newName.equals(currentPatient.getName()) && 
            newPhone.equals(currentPatient.getPhone()) && 
            newDob.equals(currentPatient.getDOB())) {
            
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Bạn chưa thay đổi thông tin nào.");
            setFieldsDisabled(true);
            return;
        }
        
        // 3. Update the currentPatient object with new values
        currentPatient.setName(newName);
        currentPatient.setPhone(newPhone);
        currentPatient.setDOB(newDob);
        currentPatient.setGender(selectedGender);

        // 4. Call updatePatient from DatabaseManager
>>>>>>> Stashed changes
        boolean success = dbManager.updatePatient(currentPatient);

        nameField.setDisable(true);
        // emailField.setDisable(true);
        femaleCheckbox.setDisable(true);
        maleCheckbox.setDisable(true);
        phoneField.setDisable(true);
        // dobPicker.setDisable(true);

        profileEditButton.setVisible(true);
        profileSaveButton.setVisible(false);

        if (name.equals(currentPatient.getName()) && phone.equals(currentPatient.getPhone())) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Bạn chưa thay đổi thông tin nào.");
            alert.showAndWait();
        }

        else if (success)
        {
            System.out.println("Nút Lưu thay đổi được nhấn!");
            System.out.println("Họ tên mới: " + name);
            System.out.println("SDT mới: " + phone);
            // System.out.println("Ngày sinh mới: " + dob);
            // System.out.println("Email mới: " + email);

            // Hiển thị thông báo thành công cho người dùng
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Đã lưu thay đổi thành công!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể lưu thay đổi. Vui lòng thử lại.");
            alert.showAndWait();
        }    
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
        // dobPicker.setValue(...);
    }

    // Phương thức này được JavaFX tự động gọi sau khi load FXML xong
    @FXML
    public void initialize() {
        // Bạn có thể load thông tin người dùng hiện tại ở đây{
        // --- Mô phỏng Đăng nhập: Tìm bệnh nhân bằng id ---
        
        String demoID = "079000000000"; // ID giả lập cho bệnh nhân demo
        
        // Gọi phương thức mới để tìm bệnh nhân
        currentPatient = DatabaseManager.getInstance().getPatientByNationalID(demoID);
        
        if (currentPatient != null) {
            setPatient(currentPatient);
        } else {
            System.err.println("Không tìm thấy bệnh nhân demo '" + demoID + "' trong cơ sở dữ liệu.");
            
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
}