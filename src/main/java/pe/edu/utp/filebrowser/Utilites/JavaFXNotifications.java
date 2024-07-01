package pe.edu.utp.filebrowser.Utilites;

import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;

/**
 * Utility class for displaying notifications using ControlsFX library.
 */
public class JavaFXNotifications {

    /**
     * Displays a notification with the given title and context text.
     *
     * @param title       the title of the notification
     * @param contextText the context text of the notification
     */
    public static void notification(String title, String contextText){
        Notifications notifications = getNotification(title, contextText);
        notifications.show();
    }

    /**
     * Displays an information notification with the given title and context text.
     *
     * @param title       the title of the notification
     * @param contextText the context text of the notification
     */
    public static void notificationInformation(String title, String contextText){
        Notifications notifications = getNotification(title, contextText);
        notifications.showInformation();
    }

    /**
     * Displays an error notification with the given title and context text.
     *
     * @param title       the title of the notification
     * @param contextText the context text of the notification
     */
    public static void notificationError(String title, String contextText){
        Notifications notifications = getNotification(title, contextText);
        notifications.showError();
    }

    /**
     * Displays a warning notification with the given title and context text.
     *
     * @param title       the title of the notification
     * @param contextText the context text of the notification
     */
    public static void notificationWarning(String title, String contextText){
        Notifications notifications = getNotification(title, contextText);
        notifications.showWarning();
    }

    /**
     * Displays a confirmation-style notification with the given title and context text.
     *
     * @param title       the title of the notification
     * @param contextText the context text of the notification
     */
    public static void notificationConfirmation(String title, String contextText){
        Notifications notifications = getNotification(title, contextText);
        notifications.showConfirm();
    }

    /**
     * Constructs and configures a Notifications object with the given title and context text.
     *
     * @param title       the title of the notification
     * @param contextText the context text of the notification
     * @return the configured Notifications object
     */
    private static Notifications getNotification(String title, String contextText){
        return Notifications.create()
                .title(title)
                .text(contextText)
                .position(Pos.BOTTOM_RIGHT)
                .darkStyle()
                .hideAfter(javafx.util.Duration.seconds(3));
    }

}
