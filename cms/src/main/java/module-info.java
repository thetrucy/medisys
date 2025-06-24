module com.medisys {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;// I add this but not really nesessary
    opens com.medisys to javafx.fxml;
    exports com.medisys;
}
