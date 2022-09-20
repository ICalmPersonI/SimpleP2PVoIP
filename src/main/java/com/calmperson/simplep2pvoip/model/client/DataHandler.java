package com.calmperson.simplep2pvoip.model.client;

import com.calmperson.simplep2pvoip.dto.Message;
import com.calmperson.simplep2pvoip.dto.Package;
import com.calmperson.simplep2pvoip.model.server.Server;
import org.apache.commons.lang3.SerializationUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;

public class DataHandler implements Runnable {

    private final DataInputStream dataInputStream;
    private final Client client;

    DataHandler(DataInputStream dataInputStream, Client client) {
        this.dataInputStream = dataInputStream;
        this.client = client;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] data = new byte[1024];
                int dataSize = dataInputStream.read(data);
                if (dataSize == -1) {
                    break;
                }
                Package pkg = SerializationUtils.deserialize(data);
                switch (pkg.getObjectName()) {
                    case Server.Obj.USERNAME_LIST: {
                        String[] usernames = (String[]) pkg.getObject();
                        client.setUsersInRoom(usernames);
                        break;
                    }
                    case Server.Obj.CHAT: {
                        Message[] chat = (Message[]) pkg.getObject();
                        client.setChat(chat);
                        break;
                    }
                }
            } catch (SocketException e) {
                System.out.printf("Client (DataHandler): %s%n", e.getMessage());
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
