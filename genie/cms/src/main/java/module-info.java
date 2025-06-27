module com.appointment {
    requires javafx.controls;
    requires javafx.fxml;
    
    opens com.appointment to javafx.fxml;
    exports com.appointment;
}