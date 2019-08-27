package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class UIMainMenuController {
    @FXML
    public Button typeInButton;
    @FXML
    public Button ePubButton;
    @FXML
    public Button readFromFileButton;

    public void setManualTextScene(ActionEvent event) throws IOException {
        new SceneLoader().loadScene(UIConstants.manualFilename + UIConstants.fxmlSuffix, event);
    }

    public void setWorkInProgressScene(ActionEvent event) throws IOException {
        new SceneLoader().loadScene(UIConstants.analysingFilename + UIConstants.fxmlSuffix, event);
    }
}
