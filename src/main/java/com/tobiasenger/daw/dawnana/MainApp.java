// File: src/main/java/com/tobiasenger/daw/dawnana/MainApp.java
package com.tobiasenger.daw.dawnana;

import com.tobiasenger.daw.dawnana.controller.DAWController;
import javafx.application.Application;
import javafx.stage.Stage;

/** Entry point for the DAW application. */
public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        new DAWController(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
