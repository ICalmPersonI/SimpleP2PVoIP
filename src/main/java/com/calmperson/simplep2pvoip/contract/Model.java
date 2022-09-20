package com.calmperson.simplep2pvoip.contract;

import com.calmperson.simplep2pvoip.dto.Message;

import java.io.IOException;

public interface Model {
    interface Server {
        void start();
        void stop();
    }

    interface Client {
        boolean connect(String name, String host, int port) throws IOException;
        void disconnect();
        void sendMessage(String message);
        Message[] getChat();
        long getConnectionDelay();
    }
}
