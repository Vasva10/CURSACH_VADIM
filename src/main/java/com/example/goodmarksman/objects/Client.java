package com.example.goodmarksman.objects;

import com.example.goodmarksman.Client.IObserver;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class Client {
    String name = "";
    Socket cs = null;
    InputStream is = null;
    OutputStream os = null;
    DataInputStream dis = null;
    DataOutputStream dos = null;
    Gson gson = new Gson();

    IObserver observer = null;

    public Client(Socket cs) {
        this.cs = cs;

        try {
            is = this.cs.getInputStream();
            dis = new DataInputStream(is);

            os = this.cs.getOutputStream();
            dos = new DataOutputStream(os);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() { return name; }

    public Socket getSocket() { return cs; }

    public void setIObserver(IObserver observer) { this.observer = observer; }
    public IObserver getIObserver() { return observer; }

    public Msg readMsg() throws IOException {
        Msg msg;
        try {
            String respStr = dis.readUTF();
            System.out.println(respStr);
            msg = gson.fromJson(respStr, Msg.class);
        } catch(IOException e) {
            System.err.println("Read msg error: " + e.getMessage());
            throw e;
        }
        return msg;
    }

    public void sendMsg(Msg msg) throws IOException {
        try {
//            System.out.println(msg);
            String strMsg = gson.toJson(msg);
            dos.writeUTF(strMsg);
        } catch (Exception e) {
            System.err.println(msg);
            System.err.println("Error in sendMessage(): " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                "cs=" + cs +
                '}';
    }
}
