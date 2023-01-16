package com.webbasedchat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

class ClientWindowWork extends JFrame {

    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private JTextField clientMess;
    private JTextField clientNameText;
    private JTextArea textArea;
    private Date dateTime;
    private String stringTime;
    private SimpleDateFormat time;
    private String clientName;

    public ClientWindowWork(String ipAddr, int port) {
        try {
            socket = new Socket(ipAddr, port);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setBounds(700, 200, 400, 350);
        setTitle("Окно клиентской части");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        textArea = new JTextArea("---- Для начала чата введите Ваше имя и нажмите \"Отправить\" ----\n");
        textArea.setEditable(false);
        JScrollPane jsp = new JScrollPane(textArea);
        add(jsp, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        clientNameText = new JTextField("Введите имя: ");
        bottomPanel.add(clientNameText, BorderLayout.WEST);
        clientMess = new JTextField("Введите сообщение: ");
        bottomPanel.add(clientMess, BorderLayout.CENTER);
        JButton jbSendMessage = new JButton("Отправить");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);

        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!clientMess.getText().trim().isEmpty() && !clientNameText.getText().trim().isEmpty()) {
                    clientName = clientNameText.getText();
                    sendMsg();
                    clientMess.grabFocus();
                }
            }
        });

        clientMess.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                clientMess.setText("");
            }
        });

        clientNameText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                clientNameText.setText("");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (in.hasNext()) {
                            String inMes = in.nextLine();
                            textArea.append(inMes + "\n");
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    dateTime = new Date();
                    time = new SimpleDateFormat("HH:mm:ss");
                    stringTime = time.format(dateTime);
                    out.println("(" + stringTime + ") " + clientName + ":" + " [--- вышел из чата ---] ");
                    out.flush();
                    out.close();
                    in.close();
                    socket.close();
                } catch (IOException exc) {
                }
            }
        });
        setVisible(true);
    }

    public void sendMsg() {

        dateTime = new Date();
        time = new SimpleDateFormat("HH:mm:ss");
        stringTime = time.format(dateTime);
        String messageStr = "(" + stringTime + ") " + clientNameText.getText() + ": " + clientMess.getText();
        out.println(messageStr);
        out.flush();
        clientMess.setText("");
    }
}

public class Client {

    public static String ipAddr = "localhost";
    public static int port = 3333;

    public static void main(String[] args) {
        new ClientWindowWork(ipAddr, port);
    }
}