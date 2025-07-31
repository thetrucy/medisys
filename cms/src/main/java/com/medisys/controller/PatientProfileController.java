package com.medisys.controller;

import com.medisys.util.DatabaseManager;

import java.io.IOException;

import com.medisys.Main;
import com.medisys.model.Patient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class PatientProfileController {
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
    private void handleProfileEditChanges() {
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
                //emailField.setDisable(false);
                femaleCheckbox.setDisable(false);
                maleCheckbox.setDisable(false);
                phoneField.setDisable(false);
                //dobPicker.setDisable(false);

                profileEditButton.setVisible(false);
                profileSaveButton.setVisible(true);
            }
        });
    }

    @FXML
    void handleProfileSaveChanges(ActionEvent event) {
        String name = nameField.getText();
        String phone = phoneField.getText();
        // String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : "";
        // String email = emailField.getText();

        // Cập nhật đối tượng hiện tại
        currentPatient.setName(name);
        currentPatient.setPhone(phone);

        // Gọi updatePatient từ DatabaseManager
        boolean success = dbManager.updatePatient(currentPatient);

        if (success)
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

            nameField.setDisable(true);
            //emailField.setDisable(true);
            femaleCheckbox.setDisable(true);
            maleCheckbox.setDisable(true);
            //dobPicker.setDisable(true);

            profileEditButton.setVisible(true);
            profileSaveButton.setVisible(false);
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
        // --- Mô phỏng Đăng nhập: Tìm bệnh nhân bằng username ---
        
        String demoUsername = "apple";
        
        // Gọi phương thức mới để tìm bệnh nhân
        currentPatient = DatabaseManager.getInstance().getPatientByUsername(demoUsername);
        
        if (currentPatient != null) {
            setPatient(currentPatient);
        } else {
            System.err.println("Không tìm thấy bệnh nhân demo '" + demoUsername + "' trong cơ sở dữ liệu.");
            
        }
    }
    
    @FXML
    private void onHomeButtonClick(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medisys/view/AppointmentOne_1.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void onAppointmentsButtonClick(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medisys/view/UpcomingApm.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void onProfileButtonClick(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medisys/view/PatientProfile.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}