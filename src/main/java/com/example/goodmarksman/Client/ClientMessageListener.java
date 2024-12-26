package com.example.goodmarksman.Client;

import com.example.goodmarksman.MainClient;
import com.example.goodmarksman.enams.ClientState;
import com.example.goodmarksman.objects.Client;
import com.example.goodmarksman.objects.Msg;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ClientMessageListener {

    private final Client server;

    ClientMessageListener(Client server){
        this.server = server;
    }

    public void messageListener() {
        while (true) {
            try {
                Msg msg = server.readMsg();
                synchronized (Thread.currentThread()) {
                    switch (msg.getAction()) {
                        case CONNECTION_ERROR:
                            server.getSocket().close();
                            break;
                        case UPDATE_GAME_STATE:
                            MainClient.m.getDao().setClientsData(msg.clientsData);
                            MainClient.m.updateState();
                            break;
                        case CLIENT_DISCONNECTED:
                            Platform.runLater(() -> {
                                try {
                                    MainClient.m.getDao().deleteClient(msg.clientData);
                                } catch (Exception e) {
                                    System.err.println("Delete Client Error in GameClient: " + e.getMessage());
                                    throw new RuntimeException(e);
                                }
                            });
                            break;
                        case CLIENT_STATE:
                            System.out.println("\nWin event: " + msg + "\n");
                            if (msg.clientState.equals(ClientState.WIN)) {
                                MainClient.game.showScoreBoard(msg.scoreBoard);
                                Platform.runLater(() ->
                                        new Alert(Alert.AlertType.INFORMATION, msg.message).show()
                                );
                            }
                            break;
                        case GET_DB:
                            MainClient.game.showScoreBoard(msg.scoreBoard);
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Message Listener error: " + e.getMessage());
                return;
            }
        }
    }

}
