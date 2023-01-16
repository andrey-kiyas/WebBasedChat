package com.webbasedchat;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

class ServerWork extends Thread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ServerWork(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        String message;
        try {
            message = in.readLine();
            //ServerForm app = new ServerForm();
            //app.setVisible(true);
            try {
                out.write(message + "\n");
                out.flush();
            } catch (IOException ignored) {
            }
            try {
                while (true) {
                    JFrame frame = new JFrame("ServerForm");
                    message = in.readLine();
                    if (message.equals("end")) {
                        this.shutDown();
                        break;
                    }
                    System.out.println("ЭХО " + message);
                    for (ServerWork sw : Server.connectList) {
                        sw.send(message);
                    }
                }
            } catch (NullPointerException ignored) {
            }
        } catch (IOException e) {
            this.shutDown();
        }
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private void shutDown() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerWork sw : Server.connectList) {
                    if (sw.equals(this)) sw.interrupt();
                    Server.connectList.remove(this);
                }
            }
        } catch (IOException ignored) {
        }
    }
}

class Server {

    public static int port = 3333;
    public static LinkedList<ServerWork> connectList = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("ЭХО-сервер запущен");
        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    connectList.add(new ServerWork(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}