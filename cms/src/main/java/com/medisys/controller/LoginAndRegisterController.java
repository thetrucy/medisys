package com.medisys.controller;

import com.medisys.model.ModelLogin;
import com.medisys.model.Patient;
import com.medisys.model.User;
import com.medisys.util.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginAndRegisterController {

    private LoginController loginController;
    private DatabaseManager dbManager = DatabaseManager.getInstance();;
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

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    @FXML
    private void register(ActionEvent event) {
        String id = txtId.getText().trim();
        String password = txtPass.getText().trim();
        String fullName = txtUser.getText().trim();
        String phone = txtPhone.getText().trim();
        boolean isDoctor = checkboxDoctor.isSelected();
        User user = new Patient(id, password, fullName, phone, isDoctor);
        loginController.registerUser(user);
        //dbManager.addUser(user);
    }

    @FXML
    private void login(ActionEvent event) {
        String id = txtIdLogin.getText().trim();
        String password = txtPassLogin.getText().trim();
        loginController.loginUser(id, password);
        // dbManager.loginUser(data); --> setcurrent user in ModelLogin
        // navigate to the main application view
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