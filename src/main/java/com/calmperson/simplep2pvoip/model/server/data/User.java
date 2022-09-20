package com.calmperson.simplep2pvoip.model.server.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class User {

    private final int id;
    private final String name;

    private final Socket voiceSocket;
    private final DataInputStream voiceInputStream;
    private final DataOutputStream voiceOutputStream;

    private final Socket dataSocket;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;

    private final Socket heartbeatSocket;
    private final DataInputStream heartbeatInputStream;
    private final DataOutputStream heartbeatOutputStream;

    private Thread voiceThread;
    private Thread dataThread;
    private Thread heartbeatHandlerThread;

    public User(int id, String name, Socket voiceSocket, DataInputStream voiceInputStream, DataOutputStream voiceOutputStream,
                Socket dataSocket, DataInputStream dataInputStream, DataOutputStream dataOutputStream,
                Socket heartbeatSocket, DataInputStream heartbeatInputStream, DataOutputStream heartbeatOutputStream) {
        this.id = id;
        this.name = name;
        this.voiceSocket = voiceSocket;
        this.voiceInputStream = voiceInputStream;
        this.voiceOutputStream = voiceOutputStream;
        this.dataSocket = dataSocket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.heartbeatSocket = heartbeatSocket;
        this.heartbeatInputStream = heartbeatInputStream;
        this.heartbeatOutputStream = heartbeatOutputStream;
    }


    public void setVoiceThread(Thread voiceThread) {
        this.voiceThread = voiceThread;
    }

    public void setDataThread(Thread dataThread) {
        this.dataThread = dataThread;
    }

    public void setHeartbeatHandlerThread(Thread heartbeatHandlerThread) {
        this.heartbeatHandlerThread = heartbeatHandlerThread;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Socket getVoiceSocket() {
        return voiceSocket;
    }

    public DataInputStream getVoiceInputStream() {
        return voiceInputStream;
    }

    public DataOutputStream getVoiceOutputStream() {
        return voiceOutputStream;
    }

    public Socket getDataSocket() {
        return dataSocket;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }


    public Socket getHeartbeatSocket() {
        return heartbeatSocket;
    }

    public DataInputStream getHeartbeatInputStream() {
        return heartbeatInputStream;
    }

    public DataOutputStream getHeartbeatOutputStream() {
        return heartbeatOutputStream;
    }

    public Thread getVoiceThread() {
        return voiceThread;
    }

    public Thread getDataThread() {
        return dataThread;
    }

    public Thread getHeartbeatHandlerThread() {
        return heartbeatHandlerThread;
    }
}
