package pe.edu.utp.filebrowser.Utilites;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import pe.edu.utp.filebrowser.FileBrowser;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * This method is called when an uncaught exception is thrown in a thread.
     * It processes the exception to find the original cause and then displays an error alert.
     *
     * @param t the thread that has an uncaught exception
     * @param e the exception that was thrown
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        /*
           Here a loop was placed that will go through all the wrappers
           of the original cause, this is mostly because when an exception
           is raised in the application, it wraps that exception in several
           exceptions like InvocationTargetException and Runtime, and in order
           to get the final cause, we would have to go through all the causes
           to get to the original.
       */
        Throwable e1 = e;
        Throwable initialCause = e.getCause();
        Throwable finalCause;
        while (initialCause!=null){
            e1 = initialCause;
            initialCause = initialCause.getCause();
        }
        finalCause = e1;

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets()
                    .add(Objects.requireNonNull(FileBrowser.class.getResource("style/style.css"))
                            .toExternalForm());
            alert.setTitle("Error");
            alert.setHeaderText("An error has occurred");
            alert.setContentText(finalCause.getMessage());
            alert.showAndWait();
        });
    }

    /**
     * Displays an information alert in the user interface.
     *
     * @param title       the title of the alert
     * @param header      the header text of the alert
     * @param contextText the body text of the alert
     */
    public static void alertInformation(String title, String header, String contextText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(FileBrowser.class.getResource("style/style.css"))
                        .toExternalForm());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(contextText);
        alert.showAndWait();
    }

    /**
     * Displays a warning alert in the user interface.
     *
     * @param title       the title of the alert
     * @param header      the header text of the alert
     * @param contextText the body text of the alert
     */
    public static void alertWarning(String title, String header, String contextText){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(FileBrowser.class.getResource("style/style.css"))
                        .toExternalForm());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(contextText);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation alert in the user interface and returns the user's response.
     *
     * @param title       the title of the alert
     * @param header      the header text of the alert
     * @param contextText the body text of the alert
     * @return the user's response as a ConfirmationOptions enum
     */
    public static ConfirmationOptions alertConfirmation(String title, String header, String contextText){
        ButtonType yes = new ButtonType("Yes"); // true
        ButtonType no = new ButtonType("No"); // false
        ButtonType cancel = new ButtonType("Cancel"); // null
        AtomicReference<ConfirmationOptions> confirmationOption = new AtomicReference<>(ConfirmationOptions.YES);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(FileBrowser.class.getResource("style/style.css"))
                        .toExternalForm());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(contextText);
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

}
