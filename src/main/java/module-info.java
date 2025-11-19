//module org.example.logit_gates {
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires javafx.web;
//
//    requires org.controlsfx.controls;
//    requires com.dlsc.formsfx;
//    requires org.kordamp.bootstrapfx.core;
//    requires eu.hansolo.tilesfx;
//    requires com.almasb.fxgl.all;
//
//    opens org.example.logit_gates to javafx.fxml;
//    exports org.example.logit_gates;
//}

module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.desktop;

    opens main to javafx.fxml;
    exports main;
}