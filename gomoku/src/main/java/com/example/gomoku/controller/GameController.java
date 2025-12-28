package com.example.gomoku.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private ImageView backgroundView;

    @FXML
    private Button playButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputStream imageStream = getClass().getResourceAsStream("/com/example/gomoku/image/background.png");
        if (imageStream != null) {
            backgroundView.setImage(new Image(imageStream));
        }
    }

    @FXML
    private void handlePlayButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/gomoku/game-view.fxml"));
            Scene gameScene = new Scene(fxmlLoader.load(), 620, 675);
            Stage stage = (Stage) playButton.getScene().getWindow();
            stage.setScene(gameScene);
            stage.setTitle("Gomoku Game");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
