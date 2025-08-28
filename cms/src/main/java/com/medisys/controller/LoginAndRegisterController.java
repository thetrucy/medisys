package com.medisys.controller;


import com.medisys.model.Patient;
import com.medisys.model.User;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginAndRegisterController {

    private LoginController loginController;
    @FXML
    private VBox registerBox;
    @FXML
    private VBox loginBox;
    @FXML
    private TextField txtUser;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtPhone;
    @FXML
    private PasswordField txtPass;
    @FXML
    private CheckBox checkboxDoctor;
    @FXML
    private TextField txtIdLogin;
    @FXML
    private PasswordField txtPassLogin;

    // --- THÊM MỚI ---
    @FXML
    private ComboBox<String> genderBoxRegister;
    // -----------------

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    // --- THÊM MỚI ---
    @FXML
    private void initialize() {
        // Khởi tạo giá trị cho ComboBox giới tính
        genderBoxRegister.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
    }
    // -----------------

    @FXML
    private void register(ActionEvent event) {
        String id = txtId.getText().trim();
        String password = txtPass.getText().trim();
        String fullName = txtUser.getText().trim();
        String phone = txtPhone.getText().trim();
        boolean isDoctor = checkboxDoctor.isSelected();

        // --- THAY ĐỔI ---
        // Lấy giá trị giới tính từ ComboBox
        String gender = genderBoxRegister.getValue();

        // Kiểm tra nếu giới tính chưa được chọn
        if (gender == null || gender.isEmpty()) {
            loginController.showMessage(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn giới tính.");
            return;
        }

        // Bác sĩ không cần giới tính, chỉ bệnh nhân mới cần
        if (checkboxDoctor.isSelected()) {
            // Logic tạo tài khoản bác sĩ (nếu cần)
        } else {
            User user = Patient.createForRegistration(id, password, fullName, phone, gender);
            loginController.registerUser(user);
        }
    }

    @FXML
    private void login(ActionEvent event) {
        String id = txtIdLogin.getText().trim();
        String password = txtPassLogin.getText().trim();
        loginController.loginUser(id, password);

    }
    @FXML
    private void forgotPassword(ActionEvent event) {
        loginController.forgotPassword();
    }

    public void showRegister(boolean show) {
        registerBox.setVisible(show);
        loginBox.setVisible(!show);
    }

}