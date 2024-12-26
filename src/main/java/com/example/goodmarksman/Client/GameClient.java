package com.example.goodmarksman.Client;

import com.example.goodmarksman.MainClient;
import com.example.goodmarksman.enams.ClientState;
import com.example.goodmarksman.models.GameModel;
import com.example.goodmarksman.objects.*;
import com.example.goodmarksman.enams.Action;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GameClient implements IObserver {
    Action gameState = Action.GAME_STOPPED;
    ClientState clientState = ClientState.NOT_READY;
    public Thread messageListener;

    private final Client server;

    // Подключение к серверу
    public GameClient(Client server) throws Exception {
        this.server = server;
        System.out.println("Client connected to " + server.getSocket().getLocalPort());

        ClientMessageListener cml = new ClientMessageListener(server);
        messageListener = new Thread(cml::messageListener);
        messageListener.setDaemon(true);
    }

    protected void showScoreBoard(ArrayList<Score> scoreBoard) {
        SB_Controller controller = new SB_Controller();

        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = MainClient.scoreBord_fxmlLoader;
                fxmlLoader.setController(controller);
                Stage stage = new Stage();
                stage.setTitle("Score Board");
                stage.setScene(new Scene(fxmlLoader.load()));
                stage.show();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });

        controller.setTable(scoreBoard);
    }

    public int getServerPort() {
        return server.getSocket().getLocalPort();
    }

    @Override
    public void event(GameModel m) {}
}
