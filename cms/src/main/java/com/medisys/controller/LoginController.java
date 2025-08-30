package com.medisys.controller;


import com.medisys.model.User;
import com.medisys.util.CurrentUser;
import com.medisys.util.DatabaseManager;

// import java.io.BufferedReader;

// import java.io.File;
// import java.io.FileReader;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.application.Platform;

public class LoginController {

    @FXML
    private BorderPane borderPane;
    @FXML
    private HBox contentBox;

    private VBox coverBox;
    private StackPane loginRegisterPane;
    private LoginAndRegisterController loginAndRegisterController;
    private CoverController coverController;
    
    private boolean isLogin = true;
    private final double coverSize = 60;
    private final double loginSize = 40;
    private DatabaseManager dbManager = DatabaseManager.getInstance();

    @FXML
    private void initialize() throws IOException {
        FXMLLoader coverLoader = new FXMLLoader(getClass().getResource("/com/medisys/view/Cover.fxml"));
        coverBox = coverLoader.load();
        coverController = coverLoader.getController();
        coverController.setLoginController(this);

        FXMLLoader loginRegisterLoader = new FXMLLoader(getClass().getResource("/com/medisys/view/LoginAndRegister.fxml"));
        loginRegisterPane = loginRegisterLoader.load();
        loginAndRegisterController = loginRegisterLoader.getController();
        loginAndRegisterController.setLoginController(this);
        Platform.runLater(() -> loginAndRegisterController.showRegister(!isLogin));

        contentBox.getChildren().addAll(loginRegisterPane, coverBox);

        coverBox.setPrefWidth(borderPane.getPrefWidth() * (coverSize / 100));
        loginRegisterPane.setPrefWidth(borderPane.getPrefWidth() * (loginSize / 100));


        HBox.setHgrow(loginRegisterPane, Priority.ALWAYS);
    }
    
    public void animateTransition() {
        Timeline timeline = new Timeline();
        KeyValue coverBoxWidth, loginRegisterPaneWidth;

        if (isLogin) {
            // Animate to register view
            coverController.updateContent(false);
            loginAndRegisterController.showRegister(true);
            coverBoxWidth = new KeyValue(coverBox.prefWidthProperty(), borderPane.getWidth() * (100 - coverSize) / 100);
            loginRegisterPaneWidth = new KeyValue(loginRegisterPane.prefWidthProperty(), borderPane.getWidth() * coverSize / 100);
        } else {
            // Animate to login view
            coverController.updateContent(true);
            loginAndRegisterController.showRegister(false);
            coverBoxWidth = new KeyValue(coverBox.prefWidthProperty(), borderPane.getWidth() * coverSize / 100);
            loginRegisterPaneWidth = new KeyValue(loginRegisterPane.prefWidthProperty(), borderPane.getWidth() * loginSize / 100);
        }
        
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), coverBoxWidth, loginRegisterPaneWidth);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        
        isLogin = !isLogin;
    }

    public void showMessage(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Validation and File I/O Logic
    public void registerUser(User user) {
        if (isEmpty(user.getName()) || isEmpty(user.getId()) || isEmpty(user.getPhone()) || isEmpty(user.getPassword())) {
            showMessage(Alert.AlertType.ERROR, "Lỗi", "Quý vị vui lòng không bỏ trống thông tin đăng ký.");
            return;
        }
        if (!isValidName(user.getName())) {
            showMessage(Alert.AlertType.ERROR, "Lỗi", "Họ và tên vừa nhập không hợp lệ. Vui lòng chỉ sử dụng chữ cái và khoảng trắng.");
            return;
        }
        if (!isValidID(user.getId())) {
            showMessage(Alert.AlertType.ERROR, "Lỗi", "Số CCCD/CMND phải là 9 hoặc 12 chữ số.");
            return;
        }
        if (!isValidPhoneNumber(user.getPhone())) {
            showMessage(Alert.AlertType.ERROR, "Lỗi", "Số điện thoại vừa nhập không hợp lệ. Phải bắt đầu bằng số 0 và có 10 chữ số.");
            return;
        }
        if (!isValidPassword(user.getPassword())) {
            showMessage(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu phải chứa ít nhất 8 ký tự, trong đó có cả chữ và số.");
            return;
        }
        // if (isDuplicateID(user.getId())) {
        //     showMessage(Alert.AlertType.ERROR, "Lỗi", "Đã có một tài khoản với số CCCD/CMND này. Xin quý vị vui lòng đăng nhập.");
        //     return;
        // }
        // CORRECTED: This now checks the correct database file via your DatabaseManager
        if (dbManager.getUserById(user.getId()) != null) {
            showMessage(Alert.AlertType.ERROR, "Lỗi", "Đã có một tài khoản với số CCCD/CMND này. Xin quý vị vui lòng đăng nhập.");
            return;
        }
        
        dbManager.addUser(user); // add user - > add patient if needed
        if(!user.isDoctor()) {
            dbManager.addPatient((com.medisys.model.Patient) user); 
        }
        showMessage(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký thành công!");
    }

    public void loginUser(String nationalId, String password) {
        User user = dbManager.loginUser(nationalId, password);
        if (user != null) {
            CurrentUser.getInstance().setCurrentUser(user);
            showMessage(Alert.AlertType.INFORMATION, "Thành công", "Đăng nhập thành công!");
            navigateToHome();
        } else {
            showMessage(Alert.AlertType.ERROR, "Lỗi", "Sai CCCD/CMND hoặc mật khẩu. Xin quý vị vui lòng nhập lại.");
        }
    }
    
    public void forgotPassword() {
        showMessage(Alert.AlertType.INFORMATION, "Quên mật khẩu", "Xin quý vị vui lòng gọi tổng đài hỗ trợ: 1900 5453");
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidName(String fullName) {
        return fullName.matches("^[\\p{L}\\s]+$");
    }

    private boolean isValidID(String id) {
        return id.matches("\\d{9}|\\d{12}");
    }

    private boolean isValidPhoneNumber(String phone) {
        //return phone.matches("^\\+?\\d{9,15}$");
        return phone.matches("^0\\d{9}$"); // Bắt đầu bằng 0, theo sau là 9 chữ số. Tổng 10 số.
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches(".*[A-Za-z].*") && password.matches(".*\\d.*");
    }

    // private boolean isDuplicateID(String id) {
    //     File file = new File("users.txt");
    //     if (!file.exists()) return false;
    //     try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
    //         String line;
    //         while ((line = reader.readLine()) != null) {
    //             String[] parts = line.split(",");
    //             if (parts.length > 0 && parts[0].equals(id)) {
    //                 return true;
    //             }
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //     return false;
    // }


    private void navigateToHome() {
        try {
            // Load the main FXML file which includes the sidebar and content area
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medisys/view/Main.fxml"));
            Parent root = loader.load();

            // Get the current stage from any component in the current scene
            Stage stage = (Stage) borderPane.getScene().getWindow();

            // Set the new scene with the main layout
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}