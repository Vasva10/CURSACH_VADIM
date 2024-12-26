package com.example.goodmarksman.models;

import com.example.goodmarksman.data.DAO;
import com.example.goodmarksman.data.DBManager;
import com.example.goodmarksman.Client.IObserver;
import com.example.goodmarksman.objects.Msg;
import com.example.goodmarksman.objects.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class GameModel {

    private static DAO dao;
    private static final ArrayList<IObserver> allO = new ArrayList<>();

    public GameModel(boolean isServer) {
        if (isServer) dao = new DBManager();
        else dao = new DAO();
    }

    public static ArrayList<IObserver> getAllO() {
        return allO;
    }

    public DAO getDao() { return dao; }

    public void event() {
        for (IObserver o: allO) {
            o.event(this);
        }
    }

    public void addObserver(IObserver o) {
        allO.add(o);
    }
    public void removeObserver(IObserver o) {
        allO.remove(o);
    }

    public int playersSize() { return dao.playersSize(); }

    public void addClient(Client cl, Arrow arrow, Score score) throws Exception {
        try {
            dao.addClient(cl, arrow, score);
            event();
        } catch (Exception e) {
            System.err.println("Error in addClient() in GameModel object: " + e.getMessage());
            throw e;
        }
    }

    public void sendMsg(Msg msg) throws IOException {
        dao.sendMsg(msg);
    }

    public ClientData getClientData(Socket s) { return dao.getClientData(s.getPort()); }

    public void setClientsData(ClientsDataArray clientsData) {
        dao.setClientsData(clientsData);
    }
    public void setClientName(Socket s, String name) {
        dao.setClientName(s, name);
    }

    public ClientsDataArray getPlayersData() { return dao.getClientsData(); }

    public Client getClient(int port) {
        if (port == -1) {
            return dao.getPlayers().get(-1);
        }

        return dao.getPlayers().get(dao.playerIndex(port));
    }
    public int getPlayerIndex(Socket s) { return dao.playerIndex(s.getPort()); }

    public void removePlayer(Client cl) { dao.removeClient(cl); }

    public void updateState() {
        if (dao.getClientsData().getArray().isEmpty()) return;
        System.out.println("update state out: " + dao.getClientsData());
        event();
    }
}
