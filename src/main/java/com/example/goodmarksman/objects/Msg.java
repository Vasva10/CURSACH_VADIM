package com.example.goodmarksman.objects;

import com.example.goodmarksman.enams.Action;
import com.example.goodmarksman.enams.ClientState;

import java.util.ArrayList;

public class Msg {
    public Action action = null;
    public ClientState clientState = null;
    public ArrayList<Score> scoreBoard = null;
    public Arrow arrow = null;
    public ClientsDataArray clientsData = null;
    public ClientData clientData = null;
    public String message = null;
    public int view_width;
    public int view_height;
    public int portOwner = -1;

    public Msg(Action action) {
        this.action = action;
    }

    public Msg(String message, Action action) {
        this.action = action;
        this.message = message;
    }

    public Msg(int[] wh, Action action) {
        this.action = action;
        this.view_width = wh[0];
        this.view_height = wh[1];
    }

    public Msg(ClientState state, Action action) {
        this.clientState = state;
        this.action = action;
    }

    public Msg(Arrow arrow, Action action) {
        this.arrow = arrow;
        this.action = action;
    }

    public Msg(ArrayList<Score> scoreBoard, Action action) {
        this.action = action;
        this.scoreBoard = scoreBoard;
    }

    public Msg(ClientsDataArray dataArray, Action action) {
        this.clientsData = dataArray;
        this.action = action;
    }

    public Msg(ClientData data, Action action) {
        this.clientData = data;
        this.action = action;
    }

    public Msg(String message, ArrayList<Score> scoreBoard, ClientState state, Action action) {
        this.message = message;
        this.clientState = state;
        this.action = action;
        this.scoreBoard = scoreBoard;
    }

    public Msg(String message) {
        this.message = message;
    }

    public Action getAction() {
        return action;
    }

//    public ArrayList<Score> getPoints() {
//        return scoreBoard;
//    }

    public Msg() {}

    @Override
    public String toString() {
        return "Msg{" +
                "action=" + action +
                ", clientState=" + clientState +
                ", scoreBoard=" + scoreBoard +
                ", arrow=" + arrow +
                ", clientsData=" + clientsData +
                ", clientData=" + clientData +
                ", message='" + message + '\'' +
                ", view_width=" + view_width +
                ", view_height=" + view_height +
                ", portOwner=" + portOwner +
                '}';
    }
}
