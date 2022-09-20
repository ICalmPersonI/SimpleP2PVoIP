package com.calmperson.simplep2pvoip.model.server;

import com.calmperson.simplep2pvoip.model.server.data.User;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;

public class VoiceHandler implements Runnable {

    private final DataInputStream voiceInputStream;
    private final User owner;
    private final int ownerId;
    private final Server server;

    VoiceHandler(User user, Server server) {
        this.voiceInputStream = user.getVoiceInputStream();
        this.owner = user;
        this.ownerId = user.getId();
        this.server = server;
    }

    @Override
    public void run() {
        byte[] voiceData = new byte[1024];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int dataSize = voiceInputStream.read(voiceData);
                if (dataSize == -1) {
                    break;
                }
                for (User u : server.getUsers()) {
                    /*
                    if (u.getId() != ownerId) {
                        u.getVoiceOutputStream().write(voiceData, 0, dataSize);
                    }

                     */
                    u.getVoiceOutputStream().write(voiceData, 0, dataSize);
                }
            } catch (SocketException e) {
                System.out.printf("Server (VoiceHandler): %s%n, user: %s", e.getMessage(), owner.getName());
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            owner.getVoiceSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
