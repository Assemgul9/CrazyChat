package com.example.demo.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(com.example.demo.demo.HelloApplication.class.getResource("hello-view.fxml"));
        stage.setTitle("Chat");
        stage.setScene(new Scene(root,600, 475));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

