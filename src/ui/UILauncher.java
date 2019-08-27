package ui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import launcher.Launcher;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class UILauncher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(UIConstants.titleFilename + UIConstants.fxmlSuffix));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(UIConstants.windowTitle);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
