package ui;

import javafx.event.ActionEvent;

import java.io.IOException;

public class SceneCommons {
    public static String enteredString;

    public static void returnToMainMenu(ActionEvent event) throws IOException {
        new SceneLoader().loadScene(UIConstants.titleFilename + UIConstants.fxmlSuffix, event);
    }
}
