package ui;

import analyser.launcher.Launcher;
import analyser.narritive_model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UILoadModelController implements Initializable {

    public Text loadedLabel;
    public Label loadingLabel;
    public TextArea textArea;
    public TextField textField;
    public Button mainMenu;
    public Text explainText;
    public Button loadModelButton;

    public void returnToMainMenu(ActionEvent event) throws IOException {
        SceneCommons.returnToMainMenu(event);
    }

    public void loadModel() throws IOException, ClassNotFoundException {
        if(textField.getText().equals("")) {
            return;
        }
        loadModelButton.setVisible(false);
        explainText.setVisible(false);
        textField.setVisible(false);
        loadingLabel.setVisible(true);
        Model model = Launcher.loadModel( UIConstants.savedModelPath + textField.getText());

        loadingLabel.setVisible(false);
        loadedLabel.setVisible(true);
        textArea.setText(Launcher.convertModelToJSON(model));
        textArea.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textArea.setVisible(false);
        textArea.setEditable(false);
        loadedLabel.setVisible(false);
        loadingLabel.setVisible(false);
    }
}
