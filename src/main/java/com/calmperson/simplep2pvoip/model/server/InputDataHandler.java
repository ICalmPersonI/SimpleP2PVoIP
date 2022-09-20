package com.calmperson.simplep2pvoip.model.server;

import com.calmperson.simplep2pvoip.dto.Message;
import com.calmperson.simplep2pvoip.dto.Package;
import com.calmperson.simplep2pvoip.model.client.Client;
import com.calmperson.simplep2pvoip.model.server.data.User;
import org.apache.commons.lang3.SerializationUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class InputDataHandler implements Runnable {
    private final DataInputStream dataInputStream;
    private final User owner;
    private final Server server;

    InputDataHandler(User user, Server server) {
        this.dataInputStream = user.getDataInputStream();
        this.owner = user;
        this.server = server;
    }

    @Override
    public void run() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] data = new byte[1024];
                int dataSize = dataInputStream.read(data);
                if (dataSize == -1) {
                    break;
                }
                Package pkg = SerializationUtils.deserialize(data);
                switch (pkg.getObjectName()) {
                    case Client.Obj.MESSAGE: {
                        String message = (String) pkg.getObject();
                        server.getChat().add(new Message(message, owner.getName(), LocalTime.now().format(timeFormat)));
                        break;
                    }
                }
            } catch (SocketException e) {
                System.out.printf("Server (InputDataHandler): %s%n, user: %s", e.getMessage(), owner.getName());
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            owner.getDataSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}