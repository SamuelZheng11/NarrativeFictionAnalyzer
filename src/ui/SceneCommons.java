package ui;

import javafx.event.ActionEvent;

import java.io.IOException;

public class SceneCommons {
    public static String targetDocument;

    public static void returnToMainMenu(ActionEvent event) throws IOException {
        new SceneLoader().loadScene(UIConstants.titleFilename + UIConstants.fxmlSuffix, event);
    }

    public static void loadAnalyserScreen(ActionEvent event) throws IOException {
        new SceneLoader().loadScene(UIConstants.analysingFilename + UIConstants.fxmlSuffix, event);
    }
}
