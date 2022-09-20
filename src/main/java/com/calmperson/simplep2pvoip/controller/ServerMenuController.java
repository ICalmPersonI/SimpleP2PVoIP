package com.calmperson.simplep2pvoip.controller;

import com.calmperson.simplep2pvoip.contract.Controller;
import com.calmperson.simplep2pvoip.contract.Model;
import com.calmperson.simplep2pvoip.model.server.Server;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ServerMenuController implements Controller.Server {

    private Model.Server server;

    private Thread serverThread;

    @FXML
    private TextField serverPort;
    @FXML
    private Button startServerButton;
    @FXML
    private Button stopServerButton;

    @FXML
    protected void onStartServerButtonClick() {
        startServer();
        startServerButton.setDisable(true);
        stopServerButton.setDisable(false);
    }

    @FXML
    protected void onStopServerButtonClick() {
        stopServer();
        startServerButton.setDisable(false);
        stopServerButton.setDisable(true);
    }

    public void stopServer() {
        server.stop();
        while (serverThread.isAlive()) {
            serverThread.interrupt();
        }
    }

    private void startServer() {
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int port = Integer.parseInt(serverPort.getText());
                server = new Server(port);
                server.start();
            }
        });
        serverThread.setName("Server");
        serverThread.start();
    }
}
