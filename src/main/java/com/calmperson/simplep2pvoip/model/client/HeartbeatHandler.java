package com.calmperson.simplep2pvoip.model.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class HeartbeatHandler implements Runnable {

    private final DataInputStream heartbeatInputStream;
    private final DataOutputStream heartbeatOutputStream;
    private final Client client;
    private LocalTime pingTime;

    HeartbeatHandler(DataInputStream dataInputStream, DataOutputStream dataOutputStream, Client client) {
        this.heartbeatInputStream = dataInputStream;
        this.heartbeatOutputStream = dataOutputStream;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            sendPing();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] data = new byte[1];
                int dataSize = heartbeatInputStream.read(data);
                if (dataSize == -1) {
                    break;
                }
                long connectionDelay = calculateConnectionDelay(pingTime);
                client.setConnectionDelay(connectionDelay);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendPing();
            } catch (SocketException e) {
                System.out.printf("Client (HeartbeatHandler): %s%n", e.getMessage());
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPing() throws IOException {
        pingTime = LocalTime.now();
        byte[] ping = new byte[1];
        heartbeatOutputStream.write(ping);
    }

    private long calculateConnectionDelay(LocalTime pong) {
        return ChronoUnit.MILLIS.between(pong, LocalTime.now());
    }
}
