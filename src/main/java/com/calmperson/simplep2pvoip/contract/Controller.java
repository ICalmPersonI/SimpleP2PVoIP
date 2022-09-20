package com.calmperson.simplep2pvoip.contract;

import com.calmperson.simplep2pvoip.dto.Message;

public interface Controller {

    interface Main {
        void closeApplication();
        void setView(View view);
    }

    interface Server {
        void stopServer();
    }

    interface Client {
        Message[] getChat();
        long getConnectionDelay();
        void disconnect();
        void sendMessage(String message);
    }
}
