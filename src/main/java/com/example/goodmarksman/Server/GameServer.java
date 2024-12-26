package com.example.goodmarksman.Server;

import com.example.goodmarksman.Client.IObserver;
import com.example.goodmarksman.MainServer;
import com.example.goodmarksman.enams.ClientState;
import com.example.goodmarksman.models.GameModel;
import com.example.goodmarksman.objects.*;
import com.example.goodmarksman.enams.Action;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.Socket;


public class GameServer implements IObserver {
    final ArrayList<Thread> messageListeners = new ArrayList<>();
    protected Thread gameThread = null;

    protected Action state = Action.NULL;
    protected boolean isPaused = false;
    protected boolean doConnectPlayers = true;
    protected int countReadyPlayers = 0;

    public GameServer() {}

    public void connectClients(ServerSocket serverSocket) {
        while (doConnectPlayers) {
            try {
                Socket cs = serverSocket.accept();
                System.out.println("Client connect (" + cs.getPort() + ")");

                Client cl = new Client(cs);
                this.addListener(cl);
            } catch (Exception e) {
                System.err.println("Error in startClientsConnection() in GameServer: " + e.getMessage());
            }
        }
    }

    public void addListener(Client cl) {
        ServerMessageListener sml = new ServerMessageListener(cl);
        Thread listenerThread = new Thread(sml::messageListener);
        listenerThread.setDaemon(true);

        messageListeners.add(listenerThread);

        cl.setIObserver((model) -> {
            try {
                Msg message = new Msg(MainServer.model.getPlayersData(), Action.UPDATE_GAME_STATE);
                System.out.println("Server Observer: " + MainServer.model.getPlayersData());

                cl.sendMsg(message);
            } catch (IOException e) {
                System.err.println("Event error: " + e.getMessage());
            }
        });

        MainServer.model.addObserver(cl.getIObserver());

        listenerThread.start();
    }

    public void addPlayer(Client cl) {
        System.out.println("players count " + MainServer.model.playersSize());
        if (MainServer.model.playersSize() >= 4) {
            try {
                cl.sendMsg(new Msg("Too many players connected to the server",
                        Action.CONNECTION_ERROR)
                );
            } catch (IOException e) {
                System.err.println("Add player Error: " + e.getMessage());
                throw new RuntimeException(e);
            }

            return;
        }

        try {
            MainServer.model.addClient(cl, new Arrow(cl.getSocket().getPort()), new Score(cl.getSocket().getPort()));
        } catch (Exception e) {
            System.err.println("Error in addPlayer() in GameServer: " + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println("new players count " + MainServer.model.playersSize());
    }

    protected void stopGame() {
        System.out.println("Stop Game was called.");

        if (state == Action.NULL ||
                gameThread == null ||
                gameThread.isInterrupted()) return;

        synchronized (this.gameThread) {
            if (!isPaused) countReadyPlayers--;
            gameThread.interrupt();
            state = Action.GAME_STOPPED;
            isPaused = false;
            gameThread.notify();
        }

        gameThread = null;

        MainServer.model.getDao().getClientsData().nullify();
        MainServer.model.event();

        synchronized (this) {
            state = Action.NULL;
        }
    }



    void run() {
        System.out.println("Game started!!!");

        while (!Thread.currentThread().isInterrupted() && state == Action.GAME_STARTED) {
            if (isPaused) {
                try {
                    synchronized (Thread.currentThread()) {
                        gameThread.wait();
                    }
                } catch (Exception e) {
                    System.err.println("Game error: " + e.getMessage());
                    stopGame();
                }
            }

            ArrayList<Arrow> arrows = MainServer.model.getDao().getClientsData().getArrows();
            ArrayList<Target> targets = MainServer.model.getDao().getClientsData().getTargets();

            synchronized (Thread.currentThread()) {
                for (Target t: targets) {
                    System.out.println(t.getY());
                    try {
                        t.move();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    System.out.println(t.getY());
                }

                for (Arrow a: arrows) {
                    if (a.getIsShooting()) {
                        a.setX(a.getX() + a.getSpeed());
                        checkIsHit(a, targets);
                    }
                }
            }

            MainServer.model.event();

            try {
                Thread.sleep(10);
            } catch(InterruptedException err) {
                System.err.println("Sleep error in game thread: " + err.getMessage());
            }
        }
    }

//    public void stopGame() {
//        gameState = Action.GAME_STOPPED;
//        gameThread.interrupt();
//        gameThread = null;
//        gameState = Action.NULL;
////        this.view.setStartPositions(target1, target2, arrow);
//    }

    private void checkIsHit(Arrow arrow, ArrayList<Target> targets) {
        System.out.println("checker: " + arrow);
        if (arrow.getX() >= arrow.getMaxX()) {
            arrow.setIsShooting(false);
            arrow.setX(arrow.getMinX());
            return;
        }

        for (Target t: targets) {
            if (arrow.getX() >= t.getX() - t.getRadius() &&
                    arrow.getX() <= t.getX() &&
                    t.isHitted(arrow.getX(), arrow.getY())) {
                arrow.hit();
                t.hit();
                int score = MainServer.model.getDao().getClientsData().updateScore(arrow.getOwnerPort(), t.getWeight());
                if (score >= 6) {
                    ArrayList<Client> players = MainServer.model.getDao().getPlayers();
                    try {
                        Client winner_cl = null;
                        for (Client cl: players) {
                            if (winner_cl == null && cl.getSocket().getPort() == arrow.getOwnerPort()) {
                                winner_cl = cl;
                            }

                            MainServer.model.getDao().insertScore(
                                    MainServer.model.getDao().getClientsData()
                                            .getScore(cl.getSocket().getPort())
                            );
                        }

                        assert winner_cl != null;
                        System.out.println("Players: " + players);
                        for (Client cl: players) {
                            if (cl == winner_cl) {
                                cl.sendMsg(new Msg(
                                        "!!!YOU WIN!!!",
                                        MainServer.model.getDao().getScoreBord("*"),
                                        ClientState.WIN,
                                        Action.CLIENT_STATE
                                ));
                            } else {
                                cl.sendMsg(new Msg(
                                        "!!!" + winner_cl.getName() + " is WINNER!!",
                                        MainServer.model.getDao().getScoreBord("*"),
                                        ClientState.WIN,
                                        Action.CLIENT_STATE
                                ));
                            }
                        }

                        stopGame();
                    } catch (IOException e) {
                        System.err.println("Win error: " + e.getMessage());
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    boolean isStarted() throws Exception {
        if (gameThread != null) {
            if (this.isPaused) {
                try {
                    synchronized (this.gameThread) {
                        this.isPaused = false;
                        gameThread.notify();
                    }
                } catch (Exception e) {
                    System.err.println("Is started method call exception: " + e.getMessage());
                    throw new Exception(e);
                }
            }
            return false;
        } else System.err.println("gameThread is null");

        return true;
    }

    public Client getLastClient() {
        return MainServer.model.getClient(-1);
    }

    @Override
    public void event(GameModel m) {}
}
