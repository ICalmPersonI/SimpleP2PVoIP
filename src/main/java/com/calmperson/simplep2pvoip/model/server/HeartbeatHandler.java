package com.calmperson.simplep2pvoip.model.server;

import com.calmperson.simplep2pvoip.model.server.data.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class HeartbeatHandler implements Runnable {

    private final DataInputStream heartbeatInputStream;
    private final DataOutputStream heartbeatOutputStream;
    private final User owner;

    HeartbeatHandler(User user) {
        this.heartbeatInputStream = user.getHeartbeatInputStream();
        this.heartbeatOutputStream = user.getHeartbeatOutputStream();
        this.owner = user;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] data = new byte[1];
                int dataSize = heartbeatInputStream.read(data);
                if (dataSize == -1) {
                    break;
                }
                sendPong();
            } catch (SocketException e) {
                System.out.printf("Server (HeartbeatHandler): %s%n, user: %s", e.getMessage(), owner.getName());
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            owner.getHeartbeatSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendPong() throws IOException {
        byte[] pong = new byte[1];
        heartbeatOutputStream.write(pong);
    }
}
