package com.example.goodmarksman.objects;

import com.example.goodmarksman.enams.COLORS;

import java.util.ArrayList;

public class ClientsDataArray {
    private ArrayList<ClientData> clientsData = new ArrayList<>();
    private ArrayList<Target> targets = new ArrayList<>();
    private final ArrayList<COLORS> freeColors = new ArrayList<>();
    private final ArrayList<COLORS> busyColors = new ArrayList<>();

    public ClientsDataArray() {
        freeColors.add(COLORS.DARK_BLUE);
        freeColors.add(COLORS.ORANGE);
        freeColors.add(COLORS.BLACK);
        freeColors.add(COLORS.PURPLE);

        targets.add(new Target(COLORS.RED, 350, 120, 10, -1, 2, 1));
        targets.add(new Target(COLORS.BLUE, 292, 120, 17, 1, 1, 2));
    }

    public int updateScore(int port, int value) {
//        if (value == 0) getData(port).getScore().shotCountInc();
        return getData(port).getScore().scoreInc(value);
    }

    public Score getScore(int port) {
        return getData(port).getScore();
    }

    public void nullify() {
        for (Target t: targets) {
            t.setY(t.getStartY());
        }

        for (ClientData c: clientsData) {
            c.getArrow().nullify();
            c.getScore().nullify();
        }
    }

    public void clearAllData() {
        clientsData.clear();
    }

    public ArrayList<Arrow> getArrows() {
        ArrayList<Arrow> arrows = new ArrayList<>();

        for (ClientData clientData : clientsData) {
            arrows.add(clientData.getArrow());
        }

        return arrows;
    }

    public void arrowShot(int port) {
        for (ClientData data : clientsData) {
            if (data.getPlayerPort() == port) {
                data.getArrow().setIsShooting(true);
                data.getScore().shotCountInc();
            }
        }

        System.err.println("Test arrow set is shot: " + getData(port).getArrow());
    }

    public void updateArrow(Arrow arrow) {
        try {
            getData(arrow.getOwnerPort()).updateArrow(arrow);
        } catch (Exception e) {
            System.err.println("Update arrow failed: " + e.getMessage());
        }
    }

    public ArrayList<Target> getTargets() { return targets; }

    //    public void add(Data data) { clientsData.add(data); }
    public ClientData add(String playerName, int socketPort, Arrow arrow, Score score) {
        arrow.setColor(freeColors.get(0));
        busyColors.add(freeColors.get(0));
        freeColors.remove(0);

        ClientData data = new ClientData(playerName, socketPort, arrow, score);
        this.clientsData.add(data);

        return data;
    }

    public ClientData getData(int socketPort) {
        for (ClientData data : this.clientsData) {
            if (data.getPlayerPort() == socketPort) {
                return data;
            }
        }
        return null;
    }

    public int getIndex(int socketPort) {
//        System.out.println(clientsData);
        //TODO: Приходит неверный сокет
        int i = 0;
        for (ClientData data : clientsData) {
            if (data.getPlayerPort() == socketPort) { return i; }
            i++;
        }
        return -1;
    }

    public void remove(int socketPort) {
        try {
            ClientData data = getData(socketPort);
            int colorInd = busyColors.indexOf(data.getArrow().getColorName());
            freeColors.add(busyColors.get(colorInd));
            busyColors.remove(colorInd);

            clientsData.remove(data);
        } catch (Exception e) {
            System.err.println("Remove client Error: " + e.getMessage());
        }
    }

    public ArrayList<ClientData> getArray() { return clientsData; }
    public void setClientsData(ClientsDataArray clientsData) {
        System.out.println("ClientsData: " + clientsData.getArray());
        this.clientsData.clear();
        this.targets.clear();

        this.clientsData = clientsData.getArray();
        this.targets = clientsData.getTargets();
    }

    public void setClientName(int port, String name) {
        getData(port).setPlayerName(name);
    }

    @Override
    public String toString() {
        return "ClientsDataArray{" +
                "clientsData=" + clientsData +
                ", targets=" + targets +
                ", freeColors=" + freeColors +
                ", busyColors=" + busyColors +
                '}';
    }
}
