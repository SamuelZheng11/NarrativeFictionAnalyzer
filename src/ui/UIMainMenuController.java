package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.swing.*;
import java.io.IOException;

public class UIMainMenuController {
    @FXML
    public Button typeInButton;
    @FXML
    public Button ePubButton;
    @FXML
    public Button readFromFileButton;

    @FXML
    public void setManualTextScene(ActionEvent event) throws IOException {
        new SceneLoader().loadScene(UIConstants.manualFilename + UIConstants.fxmlSuffix, event);
    }

    @FXML
    public void setWorkInProgressScene(ActionEvent event) throws IOException {
        new SceneLoader().loadScene(UIConstants.analysingFilename + UIConstants.fxmlSuffix, event);
    }

    @FXML
    public void setLoadModelScene(ActionEvent event) throws  IOException {
        new SceneLoader().loadScene(UIConstants.loadModelFileName + UIConstants.fxmlSuffix, event);
    }
}
