package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class UIManualTextController {
    @FXML
    public Button analyse = new Button();
    @FXML
    public TextArea textArea = new TextArea();

    @FXML
    public void sendToAnalyser(ActionEvent event) throws IOException {
        new SceneLoader().loadScene(UIConstants.analysingFilename + UIConstants.fxmlSuffix, event);
    }
}
