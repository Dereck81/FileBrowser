package pe.edu.utp.filebrowser.Utilites;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import pe.edu.utp.filebrowser.Enums.ConfirmationOptions;
import pe.edu.utp.filebrowser.FileBrowser;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Global exception handler for JavaFX applications.
 * Handles uncaught exceptions and displays corresponding alerts.
 */
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

    /**
     * Recursively finds the final cause of an exception.
     *
     * @param e the exception to find the final cause for
     * @return the final cause of the exception
     */
    private static Throwable getFinalCause(Throwable e) {
        if(e.getCause() == null) return e;

        return getFinalCause(e.getCause());
    }

    /**
     * Displays an information alert.
     *
     * @param title       the title of the alert
     * @param header      the header text of the alert
     * @param contextText the content text of the alert
     */
    public static void alertInformation(String title, String header, String contextText){
        alert(title, header, contextText, Alert.AlertType.INFORMATION);
    }

    /**
     * Displays an error alert.
     *
     * @param title       the title of the alert
     * @param header      the header text of the alert
     * @param contextText the content text of the alert
     */
    public static void alertError(String title, String header, String contextText){
        alert(title, header, contextText, Alert.AlertType.ERROR);

    }

    /**
     * Displays a warning alert.
     *
     * @param title       the title of the alert
     * @param header      the header text of the alert
     * @param contextText the content text of the alert
     */
    public static void alertWarning(String title, String header, String contextText){
        alert(title, header, contextText, Alert.AlertType.WARNING);
    }

    /**
     * Displays a confirmation alert with Yes, No, and Cancel options.
     *
     * @param title       the title of the alert
     * @param header      the header text of the alert
     * @param contextText the content text of the alert
     * @return the user's chosen ConfirmationOptions (YES, NO, or CANCEL)
     */
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

    /**
     * Helper method to create and display an alert.
     *
     * @param title       the title of the alert
     * @param header      the header text of the alert
     * @param contextText the content text of the alert
     * @param at          the type of the alert (INFORMATION, ERROR, WARNING, CONFIRMATION)
     */
    private static void alert(String title, String header, String contextText, Alert.AlertType at){
        Alert alert = getAlert(title, header, contextText, at);
        alert.showAndWait();
    }

    /**
     * Creates an Alert instance with specified properties.
     *
     * @param title       the title of the alert
     * @param header      the header text of the alert
     * @param contextText the content text of the alert
     * @param at          the type of the alert (INFORMATION, ERROR, WARNING, CONFIRMATION)
     * @return the configured Alert instance
     */
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
