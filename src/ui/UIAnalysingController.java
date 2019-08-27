package ui;

import analyser.launcher.Launcher;
import javafx.concurrent.Task;

public class UIAnalysingController {

    public UIAnalysingController() {
        new Launcher().main(new String[] {"Dorothy is a female"});
    }
}
