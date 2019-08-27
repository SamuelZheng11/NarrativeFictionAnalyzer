package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
