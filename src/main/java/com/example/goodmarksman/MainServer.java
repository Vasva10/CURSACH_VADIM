package com.example.goodmarksman;

import com.example.goodmarksman.Server.GameServer;
import com.example.goodmarksman.models.GameModel;
import com.example.goodmarksman.models.Models;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;

public class MainServer {
    public static final GameModel model = Models.buildGM(true);

    int port = 3000;
    InetAddress ip = null;

    public static GameServer game = null;

    void startServer() {
        if (game != null) return;

        ServerSocket ss;

        try {
            ip = InetAddress.getLocalHost();
            System.out.println(ip);
            ss = new ServerSocket(port, 0, ip);
            System.out.append("Server start\n");

            game = new GameServer();
            game.connectClients(ss);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        MainServer ms = new MainServer();
        ms.startServer();
    }
}
