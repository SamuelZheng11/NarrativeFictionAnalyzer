package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class UIManualTextController {
    @FXML
    public TextArea textArea = new TextArea();

    @FXML
    public void sendToAnalyser(ActionEvent event) throws IOException {
        if(textArea.getText() == null || textArea.getText().trim().equals("")) {
            return;
        }
        SceneCommons.targetDocument = textArea.getText();
        SceneCommons.loadAnalyserScreen(event);
    }
}
