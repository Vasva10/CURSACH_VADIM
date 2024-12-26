package com.example.goodmarksman.models;

public class Models {
    static GameModel gm;

    public static GameModel buildGM(boolean isServer) {
        gm = new GameModel(isServer);
        return gm;
    }
}
