package async;

import analyser.launcher.Launcher;
import analyser.narritive_model.Model;
import javafx.concurrent.Task;

public class NarrativeFictionAsyncWorker extends Task<Void> {
    private NarrativeFictionAsyncCaller caller;
    private String text;

    public NarrativeFictionAsyncWorker(NarrativeFictionAsyncCaller caller, String text) {
        this.caller = caller;
        this.text = text;
    }

    @Override
    protected Void call() {
        Model model = new Launcher().Start(text);
        this.caller.callback(model);
        return null;
    }
}
