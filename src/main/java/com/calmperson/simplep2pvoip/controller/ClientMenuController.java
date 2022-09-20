package com.calmperson.simplep2pvoip.controller;

import com.calmperson.simplep2pvoip.contract.Controller;
import com.calmperson.simplep2pvoip.contract.Model;
import com.calmperson.simplep2pvoip.dto.Message;
import com.calmperson.simplep2pvoip.model.client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ClientMenuController implements Controller.Client {

    private Model.Client client;

    @FXML
    private Button connectButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private TextField username;
    @FXML
    private TextField serverPort;
    @FXML
    private TextField serverAddress;

    @FXML
    public void initialize() {
        this.client = new Client();
    }

    @FXML
    protected void onConnectButtonClick() {
        try {
            boolean isSuccessful = client.connect(
                    username.getText().length() == 0 ? "user" : username.getText(),
                    serverAddress.getText(),
                    Integer.parseInt(serverPort.getText())
            );
            if (isSuccessful) {
                connectButton.setDisable(true);
                disconnectButton.setDisable(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connecting...");
    }

    @FXML
    protected void onDisconnectButtonClick() {
        disconnect();
    }

    @Override
    public Message[] getChat() {
        return client.getChat();
    }

    @Override
    public long getConnectionDelay() {
        return client.getConnectionDelay();
    }

    @Override
    public void disconnect() {
        client.disconnect();
        connectButton.setDisable(false);
        disconnectButton.setDisable(true);
    }

    @Override
    public void sendMessage(String message) {
        client.sendMessage(message);
    }
}
