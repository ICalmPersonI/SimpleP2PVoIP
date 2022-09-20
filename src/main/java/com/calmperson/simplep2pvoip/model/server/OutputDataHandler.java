package com.calmperson.simplep2pvoip.model.server;

import com.calmperson.simplep2pvoip.dto.Message;
import com.calmperson.simplep2pvoip.dto.Package;
import com.calmperson.simplep2pvoip.model.server.data.User;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.SocketException;

public class OutputDataHandler extends Thread {

    private final Server server;

    OutputDataHandler(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (User user : server.getUsers()) {
                    sendUsernameList(user);
                    sendChat(user);
                }
            } catch (SocketException e) {
                System.out.printf("Server (OutputDataHandler): %s%n", e.getMessage());
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1_000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendUsernameList(User user) throws IOException {
        String[] usernames = server.getUsers().stream().map(User::getName).toArray(String[]::new);
        Package<String[]> pkg = new Package<>(Server.Obj.USERNAME_LIST, usernames);
        serializationAndSent(pkg, user);
    }

    private void sendChat(User user) throws IOException {
        Package<Message[]> pkg = new Package<>(Server.Obj.CHAT, server.getChat().toArray(Message[]::new));
        serializationAndSent(pkg, user);
    }

    private void serializationAndSent(Package pack, User user) throws IOException {
        byte[] data = SerializationUtils.serialize(pack);
        user.getDataOutputStream().write(data);
    }
}
