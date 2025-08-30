package com.medisys.controller;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.medisys.model.Patient;
import com.medisys.model.User;
import com.medisys.model.UserFactory;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
    @FXML
    private DatePicker dobPickerRegister;
    @FXML
    private javafx.scene.control.Button btnRegister;
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
        // Vô hiệu hóa nút ngay khi phương thức bắt đầu
        btnRegister.setDisable(true);

        try {
            // --- THÊM MỚI: Chặn đăng ký bác sĩ ---
            if (checkboxDoctor.isSelected()) {
                loginController.showMessage(Alert.AlertType.INFORMATION, "Thông báo", "Chức năng đăng ký dành cho bác sĩ đang được phát triển. Vui lòng thử lại sau.");
                return; // Dừng thực thi ngay tại đây
            }
            // ------------------------------------

            String id = txtId.getText().trim();
            String password = txtPass.getText().trim();
            String fullName = txtUser.getText().trim();
            String phone = txtPhone.getText().trim();
            // boolean isDoctor = checkboxDoctor.isSelected();
            String gender = genderBoxRegister.getValue();
            LocalDate dobValue = dobPickerRegister.getValue();

            // Kiểm tra nếu giới tính chưa được chọn
            if (gender == null || gender.isEmpty()) {
                loginController.showMessage(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn giới tính.");
                return;
            }

            // --- THÊM MỚI: Lấy và xác thực ngày sinh ---
            String dobString = ""; // Khởi tạo chuỗi ngày sinh rỗng

            // Kiểm tra nếu ngày sinh chưa được chọn
            if (dobValue == null) {
                loginController.showMessage(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn ngày sinh.");
                return;
            }
            // Kiểm tra ngày sinh không được là ngày trong tương lai
            if (dobValue.isAfter(LocalDate.now())) {
                loginController.showMessage(Alert.AlertType.ERROR, "Lỗi", "Ngày sinh không hợp lệ (không thể là một ngày trong tương lai).");
                return;
            }

            // Định dạng ngày sinh thành chuỗi "yyyy-MM-dd" để lưu trữ
            dobString = dobValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            // ---------------------------------------------

            // Bác sĩ không cần giới tính, chỉ bệnh nhân mới cần
            if (checkboxDoctor.isSelected()) {
                // Logic tạo tài khoản bác sĩ (nếu cần)
                User user = UserFactory.createUser("doctor",id, password, fullName, phone);
                // Hiện tại chưa hỗ trợ đăng ký bác sĩ
                loginController.registerUser(user);
            } else {
                User user = UserFactory.createUser("patient",id, password, fullName, phone, gender, dobString);
                loginController.registerUser(user);
            }
        } finally {
            // Luôn bật lại nút sau khi tất cả xử lý đã xong (kể cả khi có lỗi hoặc return sớm)
            btnRegister.setDisable(false);
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