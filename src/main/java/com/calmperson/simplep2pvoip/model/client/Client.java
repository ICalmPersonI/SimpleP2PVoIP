package com.calmperson.simplep2pvoip.model.client;

import com.calmperson.simplep2pvoip.contract.Model;
import com.calmperson.simplep2pvoip.dto.Message;
import com.calmperson.simplep2pvoip.dto.Package;
import org.apache.commons.lang3.SerializationUtils;

import javax.sound.sampled.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Client implements Model.Client {

    public static class Obj {
        public static final String MESSAGE = "M";
        public static final String PING = "P";
        public static final String PONG = "PO";
    }

    private AudioFormat format;

    private Thread speaker;
    private Thread recorder;
    private Thread dataHandler;
    private Thread heartbeatHandler;

    private Socket voiceSocket;
    private DataInputStream voiceInputStream;
    private DataOutputStream voiceOutputStream;

    private Socket dataSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private Socket heartbeatSocket;
    private DataOutputStream heartbeatOutputStream;
    private DataInputStream heartbeatInputStream;

    private TargetDataLine microphone;
    private SourceDataLine speakers;

    private String[] usersInRoom;
    private Message[] chat;

    private volatile long connectionDelay;

    public Client() {
        try {
            this.format = new AudioFormat(192000.0f, 16, 2, true, false);
            this.usersInRoom = new String[0];
            this.chat = new Message[0];
            this.connectionDelay = 0;
            this.microphone = openMicrophone();
            this.speakers = openSpeakers();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean connect(String username, String host, int port) throws IOException {
        try {
            this.voiceSocket = new Socket(host, port);
            this.voiceInputStream = new DataInputStream(voiceSocket.getInputStream());
            this.voiceOutputStream = new DataOutputStream(voiceSocket.getOutputStream());

            this.dataSocket = new Socket(host, port);
            this.dataInputStream = new DataInputStream(dataSocket.getInputStream());
            this.dataOutputStream = new DataOutputStream(dataSocket.getOutputStream());

            this.heartbeatSocket = new Socket(host, port);
            this.heartbeatInputStream = new DataInputStream(heartbeatSocket.getInputStream());
            this.heartbeatOutputStream = new DataOutputStream(heartbeatSocket.getOutputStream());

            this.dataOutputStream.writeUTF(username);

            this.speaker = new Thread(new Speaker(speakers, voiceInputStream));
            this.recorder = new Thread(new Recorder(microphone, voiceOutputStream));
            this.dataHandler = new Thread(new DataHandler(dataInputStream, this));
            this.heartbeatHandler = new Thread(new HeartbeatHandler(heartbeatInputStream, heartbeatOutputStream, this));
            this.speaker.setName("Speaker");
            this.recorder.setName("Recorder");
            this.dataHandler.setName("DataHandler");
            this.heartbeatHandler.setName("HeartbeatHandler");
            this.speaker.start();
            this.recorder.start();
            this.dataHandler.start();
            this.heartbeatHandler.start();
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void disconnect() {
        try {
            usersInRoom = new String[0];
            connectionDelay = 0;
            voiceSocket.close();
            dataSocket.close();
            heartbeatSocket.close();
            while (speaker.isAlive()) {
                speaker.interrupt();
            }
            while (recorder.isAlive()) {
                recorder.interrupt();
            }
            while (dataHandler.isAlive()) {
                dataHandler.interrupt();
            }
            while (heartbeatHandler.isAlive()) {
                heartbeatHandler.interrupt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        try {
            Package<String> pkg = new Package<>(Obj.MESSAGE, message);
            byte[] data = SerializationUtils.serialize(pkg);
            dataOutputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message[] getChat() {
        return chat;
    }

    public long getConnectionDelay() {
        return connectionDelay;
    }

    public void setUsersInRoom(String[] usersInRoom) {
        this.usersInRoom = usersInRoom;
    }

    public void setChat(Message[] chat) {
        this.chat = chat;
    }

    public void setConnectionDelay(long connectionDelay) {
        this.connectionDelay = connectionDelay;
    }

    private TargetDataLine openMicrophone() throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();
        return microphone;
    }

    private SourceDataLine openSpeakers() throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(info);
        speakers.open(format);
        speakers.start();
        return speakers;
    }
}
