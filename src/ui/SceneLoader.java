package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneLoader {
    public void loadScene(String filename, ActionEvent event) throws IOException {
        Parent SceneParent = FXMLLoader.load(getClass().getResource(filename));
        Scene scene = new Scene(SceneParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.show();
    }
}
