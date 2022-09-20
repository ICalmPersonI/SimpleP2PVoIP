package com.calmperson.simplep2pvoip.view;

import com.calmperson.simplep2pvoip.contract.Controller;
import com.calmperson.simplep2pvoip.contract.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainView extends Application implements View {
    private static final int MIN_SCENE_WIDTH = 275;
    private static final int MIN_SCENE_HEIGHT = 525;

    private static final int MAX_SCENE_WIDTH = 350;
    private static final int MAX_SCENE_HEIGHT = 600;

    private Controller.Main controller;
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("/com/calmperson/simplep2pvoip/main-view.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.setView(this);

        Scene scene = new Scene(root, MIN_SCENE_WIDTH, MIN_SCENE_HEIGHT);

        scene.getStylesheets().add("style/text_field.css");
        scene.getStylesheets().add("style/button.css");
        stage.setScene(scene);

        stage.setTitle("Simple VoIP");
        stage.getIcons().add(new Image("img/phone-icon.png"));

        stage.setMinWidth(MIN_SCENE_WIDTH);
        stage.setMinHeight(MIN_SCENE_HEIGHT);

        stage.setMaxWidth(MAX_SCENE_WIDTH);
        stage.setMaxHeight(MAX_SCENE_HEIGHT);

        stage.show();
    }

    @Override
    public void stop() {
        controller.closeApplication();
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch();
    }
}