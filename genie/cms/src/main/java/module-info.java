module com.medisys {
    requires javafx.controls;
    requires javafx.fxml;
    
    opens com.medisys to javafx.fxml;
    exports com.medisys;
}