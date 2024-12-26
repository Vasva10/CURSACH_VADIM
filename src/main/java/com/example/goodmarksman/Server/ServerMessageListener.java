package com.example.goodmarksman.Server;

import com.example.goodmarksman.MainServer;
import com.example.goodmarksman.enams.Action;
import com.example.goodmarksman.enams.ClientState;
import com.example.goodmarksman.objects.Arrow;
import com.example.goodmarksman.objects.Client;
import com.example.goodmarksman.objects.Msg;
import com.example.goodmarksman.objects.Target;

public class ServerMessageListener {

    private final GameServer game = MainServer.game;
    private final Client client;

    ServerMessageListener(Client client) {
        this.client = client;
    }

    public void messageListener() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Msg msg = this.client.readMsg();

                synchronized (Thread.currentThread()) {
                    switch (msg.getAction()) {
                        case CONNECTION_ERROR:
                            throw new Exception(msg.message);
                        case CLIENT_STATE:
                            if (game.state == Action.NULL &&
                                    game.gameThread == null) {
                                game.gameThread = new Thread(game::run);
                                game.gameThread.setDaemon(true);
                                game.state = Action.GAME_STOPPED;
                            }
                            if (game.state == Action.GAME_STARTED
                                    && game.isPaused
                                    && msg.clientState == ClientState.READY) {
                                game.countReadyPlayers++;
                                if (game.countReadyPlayers == MainServer.model.playersSize())
                                    game.isStarted();
                                break;
                            } else if (msg.clientState == ClientState.NOT_READY) {
                                game.isPaused = true;
                                game.countReadyPlayers--;
                            } else if (game.state == Action.GAME_STOPPED &&
                                    msg.clientState == ClientState.READY) {
                                if (game.isStarted()) break;
                                game.countReadyPlayers++;
                                if (game.countReadyPlayers == MainServer.model.playersSize()) {
                                    game.isPaused = false;
                                    game.state = Action.GAME_STARTED;
                                    game.gameThread.start();
                                }
                            }
                            break;
                        case SET_NAME:
                            for (Client client: MainServer.model.getDao().getPlayers()) {
                                if (client.getName().equals(msg.message)) {
                                    this.client.sendMsg(new Msg(
                                            "Player with the same name is already added.",
                                            Action.CONNECTION_ERROR
                                    ));
                                    Thread.currentThread().interrupt();
                                    game.messageListeners.remove(MainServer.model.getDao().getPlayers().size());
                                    MainServer.model.removeObserver(this.client.getIObserver());
                                    return;
                                }
                            }

                            client.sendMsg(new Msg("", Action.CLIENT_CONNECTED));
                            client.setName(msg.message);
                            game.addPlayer(client);
                            break;
                        case GAME_STOPPED:
                            if (game.gameThread != null && game.gameThread.isAlive()) {
                                game.stopGame();
                            }
                            break;
                        case UPDATE_GAME_STATE:
                            if (msg.arrow != null) {
                                MainServer.model.getDao().getClientsData().updateArrow(msg.arrow);
                                MainServer.model.event();
                            }
                            break;
                        case SHOT:
                            MainServer.model.getDao().getClientsData().arrowShot(client.getSocket().getPort());
                            break;
                        case WIDTH_INIT:
                            for (Arrow a: MainServer.model.getDao().getClientsData().getArrows()) {
                                a.setMaxX(msg.view_width);
                            }
                            for (Target t: MainServer.model.getDao().getClientsData().getTargets()) {
                                t.setUpperThreshold(msg.view_height);
                            }
                            break;
                        case GET_DB:
                            client.sendMsg(new Msg(
                                    MainServer.model.getDao().getScoreBord("*"),
                                    Action.GET_DB
                            ));
                        default:
                            System.out.println("Get message: " + msg.action);
                            break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Remove is called");
                Thread.currentThread().interrupt();
                try {
                    System.err.println("Client " + client.getSocket().getPort() + " closed.");
                    System.err.println("GameServer: " + e.getMessage());

                    game.stopGame();

                    for (Client cl : MainServer.model.getDao().getPlayers()) {
                        if (cl == client) continue;
                        cl.sendMsg(new Msg(
                                MainServer.model.getClientData(cl.getSocket()),
                                Action.CLIENT_DISCONNECTED
                        ));
                    }

                    game.messageListeners.remove(MainServer.model.getPlayerIndex(client.getSocket()));
                    MainServer.model.getDao().removeClient(client);
                    MainServer.model.removeObserver(client.getIObserver());
                } catch (Exception err) {
                    System.err.println("In Err on GameServer: " + err.getMessage());
                    throw new RuntimeException(err);
                }
            }
        }
    }



}
