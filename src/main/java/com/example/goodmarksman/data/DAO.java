package com.example.goodmarksman.data;

import com.example.goodmarksman.MainClient;
import com.example.goodmarksman.enams.COLORS;
import com.example.goodmarksman.objects.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class DAO {
    private Pane gameView;
    private Circle smallTarget = null;
    private Circle bigTarget = null;
    private Polygon arrow;

    protected ArrayList<Score> DB_Score = new ArrayList<>();

    private final ArrayList<Polygon> arrows = new ArrayList<>();

    private ArrayList<Text> scoreList = new ArrayList<>();
    private ArrayList<Text> shotsList  = new ArrayList<>();
    private ArrayList<HBox> statisticBoxes = new ArrayList<>();

    private final ClientsDataArray clientsData = new ClientsDataArray();
    private final ArrayList<Client> players = new ArrayList<>();

    protected ArrayList<Score> getScoreBord(ArrayList<String> playerNames) {
        return null;
    }
    public ArrayList<Score> getScoreBord(String name) {
        return null;
    }
    public void insertScore(Score score) throws Exception {}

    public int playersSize() {
        return players.size();
    }

    public void addClient(Client cl, Arrow arrow, Score score) throws Exception {
        if (players.contains(cl) || clientsData.getIndex(cl.getSocket().getPort()) != -1) {
            throw new Exception("Player is already added.");
        }

        players.add(cl);
        clientsData.add("", cl.getSocket().getPort(),
                arrow, score).setPlayerName(cl.getName());
    }

    public ArrayList<Client> getPlayers() { return players; }

    public void sendMsg(Msg msg) throws IOException {
        if (msg.portOwner == -1) throw new IOException("Port owner not set.");
        Client client = players.get(playerIndex(msg.portOwner));
        client.sendMsg(msg);
    }

    public void setClientName(Socket s, String name) {
        clientsData.setClientName(s.getPort(), name);
    }

    public void removeClient(Client cl) {
        clientsData.remove(cl.getSocket().getPort());
        players.remove(cl);
    }

    public int playerIndex(int port) {
        return clientsData.getIndex(port);
    }

    public ClientData getClientData(int port) {
        return clientsData.getData(port);
    }

    public ClientsDataArray getClientsData() {
        return clientsData;
    }

    public void setClientsData(ClientsDataArray clientsData) {
        this.clientsData.setClientsData(clientsData);
    }

    public Pane getGameView() { return gameView; }
    public void setGameView(Pane gameView) { this.gameView = gameView; }

    public Polygon getArrow() { return arrow; }
    public void setArrow(Polygon arrow) { this.arrow = arrow; }

    public ArrayList<Polygon> getArrows() {
        ArrayList<Polygon> a = new ArrayList<>();
        if (arrow != null) a.add(this.arrow);
        if (!arrows.isEmpty()) a.addAll(getArrows());

        return a;
    }

    public ArrayList<Text> getScoreList() { return scoreList; }
    public void setScoreList(ArrayList<Text> scoreList) { this.scoreList = scoreList; }

    public ArrayList<Text> getShotsList() { return shotsList; }
    public void setShotsList(ArrayList<Text> shotsList) { this.shotsList = shotsList; }

    public ArrayList<HBox> getStatisticBoxes() { return statisticBoxes; }
    public void setStatisticBoxes(ArrayList<HBox> statisticBoxes) {
        System.err.println("DAO: " + statisticBoxes);
        this.statisticBoxes = statisticBoxes;
        System.err.println("DAO: " + this.statisticBoxes);
    }

    public Polygon getEnemyArrow(COLORS color) {
        if (arrows.isEmpty()) return null;

        for (Polygon arrow : arrows) {
            if (arrow.getFill().equals(color.getValue())) return arrow;
        }

        return null;
    }

    public void updateTargets() {
        ArrayList<Target> targets = clientsData.getTargets();
        Platform.runLater(() -> {
            Target target1 = targets.get(0);
            Target target2 = targets.get(1);
            if (target1.getRadius() > target2.getRadius()) {
                Target tmp = target1;
                target1 = target2;
                target2 = tmp;
            }

            if (smallTarget == null || bigTarget == null) {
                smallTarget = target1.getCircle();
                bigTarget = target2.getCircle();

                gameView.getChildren().add(smallTarget);
                gameView.getChildren().add(bigTarget);
            } else {
                smallTarget.setLayoutY(target1.getY());
                smallTarget.setFill(target1.getCurrentColor().getValue());

                bigTarget.setLayoutY(target2.getY());
                bigTarget.setFill(target2.getCurrentColor().getValue());
            }
        });
    }

    public void updateArrows() {
        for (ClientData data : clientsData.getArray()) {
            if (data.getArrow().getOwnerPort() == -1) { throw new NullPointerException(); }

            if (data.getArrow().getOwnerPort() == MainClient.game.getServerPort()) {
                Platform.runLater(() -> {
                    this.arrow.setLayoutX(data.getArrow().getX());
                    this.arrow.setLayoutY(data.getArrow().getY());
//                System.err.println(this.arrow);

                    try {
                        this.arrow.setFill(data.getArrow().getColorName().getValue());
                    } catch (Error e) {
                        System.err.println(e.getMessage());
                    }
                });
            } else {
//            System.err.println("Enemy color is: " + arrow.getColorName());
                Platform.runLater(() -> {
                    Polygon poly = getEnemyArrow(data.getArrow().getColorName());
                    if (poly == null) {
                        poly = data.getArrow().getPolygon();
                        this.gameView.getChildren().add(poly);
                    }
                    arrows.add(poly);
//                System.err.println(poly);

                    poly.setLayoutX(data.getArrow().getX());
                    poly.setLayoutY(data.getArrow().getY());
                    try {
                        poly.setFill(data.getArrow().getColorName().getValue());
                    } catch (Error e) {
                        System.err.println(e.getMessage());
                    }
                });
            }
        }
    }

    public void deleteClient(ClientData clientData) throws Exception {
        for (Polygon a : arrows) {
            if (a.getFill().equals(clientData.getArrow().getColorName().getValue())) {
                gameView.getChildren().remove(a);
                break;
            }
        }

        try {
            int i = 0;
            for (HBox box: statisticBoxes) {
                Circle circle = null;
                Label label = null;
                for (Node node : box.getChildren()) {
                    if (node instanceof Circle) {
                        circle = (Circle) node;
                    } else if (node instanceof Label) {
                        label = (Label) node;
                    }
                }


                if (circle != null && label != null &&
                        label.getText().equals(clientData.getPlayerName())) {


                    circle.setFill(Color.TRANSPARENT);
                    label.setText("");
                    break;
                } else {
                    i++;
                }
            }

            scoreList.get(i).setText("0");
            shotsList.get(i).setText("0");
        } catch (Exception e) {
            System.err.println("Delete Client Error: " + e.getMessage());
            throw e;
        }
    }

    public void updateScores() {
        int i = 1;
        for (ClientData data : clientsData.getArray()) {
            if (data.getScore().getOwnerPort() == -1) { throw new NullPointerException(); }

            if (data.getScore().getOwnerPort() == MainClient.game.getServerPort()) {
                System.err.println("children: " + statisticBoxes.get(0).getChildren());
                Platform.runLater(() -> {
                    for (Node node: statisticBoxes.get(0).getChildren()) {
                        if (node instanceof Circle circle) {
                            circle.setFill(data.getArrow().getColorName().getValue());
                        } else if (node instanceof Label label) {
                            label.setText(data.getScore().getPlayerName());
                        }
                    }

                    this.scoreList.get(0).setText(String.valueOf(data.getScore().getScoreValue()));
                    this.shotsList.get(0).setText(String.valueOf(data.getScore().getShotCountValue()));
                });
            } else {
                System.err.println("score: " + data.getScore());
                int iter = i;
                Platform.runLater(() -> {
                    Circle circle = null;
                    Label label = null;
                    for (Node node: statisticBoxes.get(iter).getChildren()) {
                        if (node instanceof Circle)
                            circle = (Circle) node;
                        else if (node instanceof Label)
                            label = (Label) node;
                    }

                    if (circle != null && label != null) {
                        if (label.getText().isEmpty()) {
                            circle.setFill(data.getArrow().getColorName().getValue());
                            label.setText(data.getScore().getPlayerName());
                        }

                        this.scoreList.get(iter).setText(String.valueOf(data.getScore().getScoreValue()));
                        this.shotsList.get(iter).setText(String.valueOf(data.getScore().getShotCountValue()));
                    }
                });
                ++i;
            }
        }
    }
}
