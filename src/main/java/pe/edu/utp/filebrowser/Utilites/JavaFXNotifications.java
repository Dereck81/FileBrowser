package pe.edu.utp.filebrowser.Utilites;

import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;

public class JavaFXNotifications {

    public static void notification(String title, String contextText){
        Notifications notifications = getNotification(title, contextText);
        notifications.show();
    }

    public static void notificationInformation(String title, String contextText){
        Notifications notifications = getNotification(title, contextText);
        notifications.showInformation();
    }

    public static void notificationError(String title, String contextText){
        Notifications notifications = getNotification(title, contextText);
        notifications.showError();
    }

    public static void notificationWarning(String title, String contextText){
        Notifications notifications = getNotification(title, contextText);
        notifications.showWarning();
    }

    public static void notificationConfirmation(String title, String contextText){
        Notifications notifications = getNotification(title, contextText);
        notifications.showConfirm();
    }

    private static Notifications getNotification(String title, String contextText){
        return Notifications.create()
                .title(title)
                .text(contextText)
                .position(Pos.BOTTOM_RIGHT)
                .darkStyle()
                .hideAfter(javafx.util.Duration.seconds(3));
    }

}
