package com.calmperson.simplep2pvoip.model.server;

import com.calmperson.simplep2pvoip.contract.Model;
import com.calmperson.simplep2pvoip.dto.Message;;
import com.calmperson.simplep2pvoip.model.server.data.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server implements Model.Server {

    public static class Obj {
        public static final String USERNAME_LIST = "UL";
        public static final String CHAT = "C";
        public static final String PING = "P";
        public static final String PONG = "PO";
    }

    private final int port;
    private final List<User> users;
    private final List<Message> chat;
    private ServerSocket serverSocket;

    private final Thread outputDataHandler;
    private final Thread usersManager;

    private int nextId;

    public Server(int port) {
        this.port = port;
        this.users = Collections.synchronizedList(new ArrayList<>());
        this.chat = Collections.synchronizedList(new ArrayList<>());
        this.outputDataHandler = new OutputDataHandler(this);
        this.usersManager = new UsersManager();
        this.nextId = 1;
        this.outputDataHandler.setName("OutputDataHandler");
        this.usersManager.setName("UserManager");
    }

    @Override
    public void start() {
        outputDataHandler.start();
        usersManager.start();
        try {
            serverSocket = new ServerSocket(port, 2);
            System.out.println("Server started!");
            while (!Thread.currentThread().isInterrupted()) {

                Socket voiceSocket = serverSocket.accept();
                DataInputStream voiceInputStream = new DataInputStream(voiceSocket.getInputStream());
                DataOutputStream voiceOutputStream = new DataOutputStream(voiceSocket.getOutputStream());

                Socket dataSocket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(dataSocket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(dataSocket.getOutputStream());

                Socket heartbeatSocket = serverSocket.accept();
                DataInputStream heartbeatInputStream = new DataInputStream(heartbeatSocket.getInputStream());
                DataOutputStream heartbeatOutputStream = new DataOutputStream(heartbeatSocket.getOutputStream());

                String username = dataInputStream.readUTF();
                User user = new User(
                        nextId,
                        username,
                        voiceSocket, voiceInputStream, voiceOutputStream,
                        dataSocket, dataInputStream, dataOutputStream,
                        heartbeatSocket, heartbeatInputStream, heartbeatOutputStream
                );

                user.setDataThread(new Thread(new InputDataHandler(user, this)));
                user.setVoiceThread(new Thread(new VoiceHandler(user, this)));
                user.setHeartbeatHandlerThread(new Thread(new HeartbeatHandler(user)));

                user.getDataThread().setName(String.format("%s's DataThread", username));
                user.getVoiceThread().setName(String.format("%s's VoiceThread", username));
                user.getHeartbeatHandlerThread().setName(String.format("%s's HeartbeatHandler", username));

                user.getDataThread().start();
                user.getVoiceThread().start();
                user.getHeartbeatHandlerThread().start();

                users.add(user);
                nextId++;
                System.out.println(username + " Connected!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            for (User user : users) {
                user.getVoiceSocket().close();
                user.getDataSocket().close();
            }
            serverSocket.close();
            while (outputDataHandler.isAlive()) {
                outputDataHandler.interrupt();
            }
            while (usersManager.isAlive()) {
                usersManager.interrupt();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Message> getChat() {
        return chat;
    }

    class UsersManager extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                ArrayList<User> usersList = new ArrayList<>(users);
                for (User user : usersList) {
                    if (user.getVoiceSocket().isClosed() && user.getDataSocket().isClosed()) {
                        users.remove(user);
                        System.out.printf("%s disconnected.\n", user.getName());
                    }
                }
            }
        }
    }
}