module com.tobiasenger.daw.dawnana {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.media;

    opens com.tobiasenger.daw.dawnana to javafx.fxml;
    exports com.tobiasenger.daw.dawnana;
    exports com.tobiasenger.daw.dawnana.controller;
    opens com.tobiasenger.daw.dawnana.controller to javafx.fxml;
}