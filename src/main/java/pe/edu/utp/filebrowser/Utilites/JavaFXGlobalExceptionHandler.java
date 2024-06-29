package pe.edu.utp.filebrowser.Utilites;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import pe.edu.utp.filebrowser.Enums.ConfirmationOptions;
import pe.edu.utp.filebrowser.FileBrowser;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class JavaFXGlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * This method is called when an uncaught exception is thrown in a thread.
     * It processes the exception to find the original cause and then displays an error alert.
     *
     * @param t the thread that has an uncaught exception
     * @param e the exception that was thrown
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Platform.runLater(() ->
            alertError(
                    "Error",
                    "An error has ocurred",
                    getFinalCause(e).getMessage())
        );
    }

    /*
       Here a loop was placed that will go through all the wrappers
       of the original cause, this is mostly because when an exception
       is raised in the application, it wraps that exception in several
       exceptions like InvocationTargetException and Runtime, and in order
       to get the final cause, we would have to go through all the causes
       to get to the original.
    */
    private static Throwable getFinalCause(Throwable e) {
        if(e.getCause() == null) return e;
        return getFinalCause(e.getCause());
    }

    public static void alertInformation(String title, String header, String contextText){
        alert(title, header, contextText, Alert.AlertType.INFORMATION);
    }

    public static void alertError(String title, String header, String contextText){
        alert(title, header, contextText, Alert.AlertType.ERROR);

    }

    public static void alertWarning(String title, String header, String contextText){
        alert(title, header, contextText, Alert.AlertType.WARNING);
    }

    public static ConfirmationOptions alertConfirmation(String title, String header, String contextText){
        ButtonType yes = new ButtonType("Yes"); // true
        ButtonType no = new ButtonType("No"); // false
        ButtonType cancel = new ButtonType("Cancel"); // null
        AtomicReference<ConfirmationOptions> confirmationOption = new AtomicReference<>(ConfirmationOptions.YES);
        Alert alert = getAlert(title, header, contextText, Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(yes, no, cancel);
        alert.showAndWait().ifPresent(action -> {
            if(action == yes)
                confirmationOption.set(ConfirmationOptions.YES);
            else if (action == no)
                confirmationOption.set(ConfirmationOptions.NO);
            else
                confirmationOption.set(ConfirmationOptions.CANCEL);
        });
        return confirmationOption.get();
    }

    private static void alert(String title, String header, String contextText, Alert.AlertType at){
        Alert alert = getAlert(title, header, contextText, at);
        alert.showAndWait();
    }

    private static Alert getAlert(String title, String header, String contextText, Alert.AlertType at){
        Alert alert = new Alert(at);
        alert.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(FileBrowser.class.getResource("style/style.css"))
                        .toExternalForm());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(contextText);
        return alert;
    }

}
