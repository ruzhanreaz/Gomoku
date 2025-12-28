package com.example.gomoku;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home-view.fxml"));
        Parent content = fxmlLoader.load();

        BorderPane root = new BorderPane();
        root.setCenter(content);

        Scene scene = new Scene(root, 820, 670);
        stage.setTitle("Gomoku");
        stage.setScene(scene);
        stage.show();
    }
}
