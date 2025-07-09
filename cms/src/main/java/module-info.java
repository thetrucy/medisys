module com.medisys {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;// I add this but not really nesessary
    requires java.sql;
    opens com.medisys.controller to javafx.fxml;
    opens com.medisys to java.fxml;

    exports com.medisys.controller;
    exports com.medisys.util;
    exports com.medisys.model;
    exports com.medisys;
}
