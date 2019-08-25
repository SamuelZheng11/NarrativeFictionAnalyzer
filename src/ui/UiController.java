package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import launcher.Launcher;

public class UiController {
    public Button typeInButton = new Button("Type in your own narrative fiction");
    public Button ePubButton = new Button("Get narrative from ePub");
    public Button readFromFileButton = new Button("Read in narrative from file");


    public void startAnaalyser() {
        Launcher.main(null);
    }
}
