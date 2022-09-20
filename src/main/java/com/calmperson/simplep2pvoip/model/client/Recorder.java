package com.calmperson.simplep2pvoip.model.client;

import javax.sound.sampled.TargetDataLine;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;

public class Recorder implements Runnable {

    private final TargetDataLine microphone;
    private final DataOutputStream voiceOutputStream;

    Recorder(TargetDataLine microphone, DataOutputStream voiceOutputStream) {
        this.microphone = microphone;
        this.voiceOutputStream = voiceOutputStream;
    }

    @Override
    public void run() {
        byte[] data = new byte[1024];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int dataSize = microphone.read(data, 0, 1024);
                if (dataSize == -1) {
                    break;
                }
                voiceOutputStream.write(data, 0, dataSize);
            } catch (SocketException e) {
                System.out.printf("Client (Recorder): %s%n", e.getMessage());
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //microphone.drain();
    }
}
