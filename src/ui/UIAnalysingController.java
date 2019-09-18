package ui;

import analyser.launcher.Launcher;
import analyser.narritive_model.Model;
import async.NarrativeFictionAsyncCaller;
import async.NarrativeFictionAsyncWorker;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UIAnalysingController implements NarrativeFictionAsyncCaller, Initializable {

    @FXML
    public Label label;
    public Model model;
    public TextArea textArea;
    public TextField textField;
    public Task<Void> task;

    public UIAnalysingController() {
        task = new NarrativeFictionAsyncWorker(this, SceneCommons.enteredString);
    }

    @Override
    public void callback(Model model) {
        this.model = model;
        label.setVisible(false);
        textArea.setText(Launcher.convertModelToJSON(model));
        textArea.setVisible(true);
    }

    public void returnToMenu(ActionEvent event) throws IOException {
        SceneCommons.returnToMainMenu(event);
    }

    public void saveModel() {
        if(textField.getText().equals("")) {
            return;
        }

        String filename = textField.getText();
        try {
            Launcher.saveModel(model, UIConstants.savedModelPath + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textArea.setVisible(false);
        textArea.setEditable(false);
        Thread thread = new Thread(task);
        thread.start();
    }
}
