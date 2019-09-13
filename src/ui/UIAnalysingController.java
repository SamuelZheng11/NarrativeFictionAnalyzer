package ui;

import analyser.narritive_model.Model;
import async.NarrativeFictionAsyncCaller;
import async.NarrativeFictionAsyncWorker;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;


public class UIAnalysingController implements NarrativeFictionAsyncCaller, Initializable {

    @FXML
    public Label label;
    public Model model;
    public Task<Void> task;

    public UIAnalysingController() {
        task = new NarrativeFictionAsyncWorker(this, SceneCommons.enteredString);
    }

    @Override
    public void callback(Model model) {
        this.model = model;
    }

    public void inspectModel() {
        Model item = this.model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Thread thread = new Thread(task);
        thread.start();
    }
}
