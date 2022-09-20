package com.calmperson.simplep2pvoip.model.client;

import javax.sound.sampled.SourceDataLine;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;

public class Speaker implements Runnable {

    private final SourceDataLine speakers;
    private final DataInputStream voiceInputStream;

    Speaker(SourceDataLine speakers, DataInputStream voiceInputStream) {
        this.speakers = speakers;
        this.voiceInputStream = voiceInputStream;
    }

    @Override
    public void run() {
        byte[] data = new byte[1024];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int dataSize = voiceInputStream.read(data);
                if (dataSize == -1) {
                    break;
                }
                try {
                    speakers.write(data, 0, dataSize);
                } catch (IllegalArgumentException e) {
                    //e.printStackTrace();
                }
            } catch (SocketException e) {
                System.out.printf("Client (Speaker): %s%n", e.getMessage());
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        speakers.drain();
    }
}
