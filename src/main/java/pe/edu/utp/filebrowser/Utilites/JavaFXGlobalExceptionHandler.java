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
        return alertConfirmation(title, header, contextText,
                ConfirmationOptions.YES, ConfirmationOptions.NO, ConfirmationOptions.CANCEL);
    }

    /**
     * Displays a confirmation alert with three customizable options.
     *
     * @param title       the title of the alert
     * @param header      the header text of the alert
     * @param contextText the content text of the alert
     * @param option1     the first option to display on the alert
     * @param option2     the second option to display on the alert
     * @param option3     the third option to display on the alert
     * @param <ENM>       the enum type of the options
     * @return the user's chosen option (one of option1, option2, or option3)
     */
    public static <ENM> ENM alertConfirmation(String title, String header, String contextText,
                                              ENM option1, ENM option2, ENM option3){

        if(!option1.getClass().isEnum())
            throw new IllegalArgumentException("The options entered are not an enumeration.");

        ButtonType BT1 = new ButtonType(capitalize(option1.toString().toLowerCase()));
        ButtonType BT2 = new ButtonType(capitalize(option2.toString().toLowerCase()));
        ButtonType BT3 = new ButtonType(capitalize(option3.toString().toLowerCase()));

        AtomicReference<ENM> confirmationOption = new AtomicReference<>(option1);

        Alert alert = getAlert(title, header, contextText, Alert.AlertType.CONFIRMATION);

        alert.getButtonTypes().setAll(BT1, BT2, BT3);
        alert.showAndWait().ifPresent(action -> {
            if(action == BT1)
                confirmationOption.set(option1);
            else if (action == BT2)
                confirmationOption.set(option2);
            else
                confirmationOption.set(option3);
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

    /**
     * Capitalizes the first character of the given text.
     *
     * @param text the input text to be capitalized
     * @return the capitalized text, or an empty string if the input is null, empty, or blank
     */
    private static String capitalize(String text){
        if(text == null || text.isEmpty() || text.isBlank())
            return null;

        return String.valueOf(text.charAt(0)).toUpperCase()+text.substring(1);
    }

}
