package com.medisys.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CoverController {
    private LoginController loginController;

    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private Label description1;
    @FXML
    private Button button;

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    @FXML
    private void buttonClick(ActionEvent event) {
        loginController.animateTransition();
    }

    public void updateContent(boolean isLogin) {
        if (isLogin) {
            title.setText("Kính chào quý vị");
            description.setText("Vui lòng đăng ký tại đây");
            description1.setText("nếu quý vị chưa có tài khoản");
            button.setText("ĐĂNG KÝ");
        } else {
            title.setText("Chào mừng trở lại");
            description.setText("Vui lòng đăng nhập tại đây");
            description1.setText("nếu quý vị đã có tài khoản");
            button.setText("ĐĂNG NHẬP");
        }
    }
}