package com.calmperson.simplep2pvoip.controller;

import com.calmperson.simplep2pvoip.contract.Controller;
import com.calmperson.simplep2pvoip.contract.Model;
import com.calmperson.simplep2pvoip.model.client.Client;
import com.calmperson.simplep2pvoip.view.MainView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import com.calmperson.simplep2pvoip.contract.View;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainController implements Controller.Main {

    private View view;

    private Controller.Server serverController;
    private Controller.Client clientController;

    private Thread chatUpdaterThread;
    private Thread pingUpdaterThread;

    private Popup clientMenu;
    private Popup serverMenu;

    @FXML
    private TextField message;
    @FXML
    private Button clientMenuButton;
    @FXML
    private Button serverMenuButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Label pingValue;
    @FXML
    private Button sendMessage;
    @FXML
    private ListView<String> chat;

    private final Runnable chatUpdater = new Runnable() {
        @Override
        public void run() {
            ObservableList<String> chatList = chat.getItems();
            while (!Thread.currentThread().isInterrupted()) {
                List<String> messageAsString = Arrays.stream(clientController.getChat())
                        .map(m -> String.format("%s: %s %s", m.getAuthor(), m.getText(), m.getDate()))
                        .collect(Collectors.toList());
                Platform.runLater(() -> chatList.setAll(messageAsString));
                try {
                    Thread.sleep(1_000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private final Runnable pingUpdater = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                long connectionDelay = Math.abs(clientController.getConnectionDelay());
                String ping = String.valueOf(connectionDelay);
                Platform.runLater(() -> pingValue.setText(ping));
                if (connectionDelay > 1000) {
                    clientController.disconnect();
                }

                try {
                    Thread.sleep(1_000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @FXML
    public void initialize() {
        initClientMenu();
        initServerMenu();
        this.chatUpdaterThread = new Thread(chatUpdater);
        this.pingUpdaterThread = new Thread(pingUpdater);
        this.chatUpdaterThread.setName("ChatUpdater");
        this.pingUpdaterThread.setName("PingUpdater");
        this.chatUpdaterThread.start();
        this.pingUpdaterThread.start();

        this.clientMenuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (clientMenu.isShowing()) {
                    clientMenu.hide();
                } else {
                    clientMenu.show(view.getStage(), event.getScreenX(), event.getScreenY() + 25);
                }
            }
        });

        this.serverMenuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (serverMenu.isShowing()) {
                    serverMenu.hide();
                } else {
                    serverMenu.show(view.getStage(), event.getScreenX(), event.getScreenY() + 25);
                }
            }
        });
    }

    @FXML
    protected void onSendMessageButtonClick() {
        clientController.sendMessage(message.getText());
    }

    @Override
    public void closeApplication() {
        if (chatUpdaterThread != null) {
            while (chatUpdaterThread.isAlive()) {
                chatUpdaterThread.interrupt();
            }
        }
        if (pingUpdaterThread != null) {
            while (pingUpdaterThread.isAlive()) {
                pingUpdaterThread.interrupt();
            }
        }
        serverController.stopServer();
        clientController.disconnect();
    }


    @Override
    public void setView(View view) {
        this.view = view;
    }

    private void initClientMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(MainView.class.getResource("/com/calmperson/simplep2pvoip/client-menu-view.fxml"));
            Parent root = loader.load();
            clientController = loader.getController();
            Scene scene = new Scene(root);

            Popup popup = new Popup();
            popup.getContent().setAll(scene.getRoot());
            clientMenu = popup;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initServerMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(MainView.class.getResource("/com/calmperson/simplep2pvoip/server-menu-view.fxml"));
            Parent root = loader.load();
            serverController = loader.getController();
            Scene scene = new Scene(root);

            Popup popup = new Popup();
            popup.getContent().setAll(scene.getRoot());
            serverMenu = popup;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}