package com.example.goodmarksman.objects;

import javafx.scene.shape.Polygon;

public class ClientData {
    private String playerName;
    private int playerPort;

    private Arrow arrow;
    private Score score;

    public ClientData(String playerName, int playerPort, Arrow arrow, Score score) {
        if (playerName == null || playerName.isEmpty()) this.playerName = "undefined";
        else this.playerName = playerName;

        this.playerPort = playerPort;
        this.arrow = arrow;
        this.score = score;
    }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        this.score.setPlayerName(playerName);
    }

    public int getPlayerPort() { return this.playerPort; }
    public void setPlayerPort(int playerPort) { this.playerPort = playerPort; }

    public Arrow getArrow() { return arrow; }
    public void setArrow(Arrow arrow) { this.arrow = arrow; }

    public void updateArrow(Arrow arrow) {
        this.arrow.setX(arrow.getX());
        this.arrow.setY(arrow.getY());
    }

    public Score getScore() { return score; }
    public void setScore(Score score) { this.score = score; }

    @Override
    public String toString() {
        return "ClientData{" +
                "playerName='" + playerName + '\'' +
                ", playerPort=" + playerPort +
                ", arrow=" + arrow +
                ", score=" + score +
                '}';
    }
}
