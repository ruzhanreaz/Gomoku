module com.example.gomoku {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;


    opens com.example.gomoku to javafx.fxml;
    opens com.example.gomoku.controller to javafx.fxml;
    exports com.example.gomoku;
    exports com.example.gomoku.controller;
}