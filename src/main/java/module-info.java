module uefs.sistemavendas3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.google.gson;
    requires junit;

    opens uefs.vendaingressos to javafx.fxml;
    exports uefs.vendaingressos;
    opens uefs.vendaingressos.model to com.google.gson;


}