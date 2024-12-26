package com.example.goodmarksman;

import com.example.goodmarksman.Client.GameClient;
import com.example.goodmarksman.models.GameModel;
import com.example.goodmarksman.models.Models;
import com.example.goodmarksman.objects.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainClient extends Application {
    public static final GameModel m = Models.buildGM(false);
    public static GameClient game = null;
    public static Client server = null;
    public static String playerName = "";
    public static Stage primaryStage;

    public static FXMLLoader game_fxmlLoader = new FXMLLoader(MainClient.class.getResource("game-view.fxml"));
    public static FXMLLoader scoreBord_fxmlLoader = new FXMLLoader(MainClient.class.getResource("score-bord.fxml"));

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainClient.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage = stage;
        stage.setTitle("Good Marksman!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}