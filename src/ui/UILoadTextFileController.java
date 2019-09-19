package ui;

import analyser.launcher.Launcher;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class UILoadTextFileController {

    public Text loadedLabel;
    public TextField textField;
    public Button mainMenu;
    public Text explainText;
    public Button loadModelButton;

    public void returnToMainMenu(ActionEvent event) throws IOException {
        SceneCommons.returnToMainMenu(event);
    }

    public void loadModel(ActionEvent event) throws IOException {
        if(textField.getText().equals("")) {
            return;
        }

        SceneCommons.targetDocument = Launcher.makeDocumentFromTextFile(UIConstants.textDocumentPath + textField.getText());
        SceneCommons.loadAnalyserScreen(event);
    }
}
