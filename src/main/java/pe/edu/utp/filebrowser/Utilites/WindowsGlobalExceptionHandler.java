package pe.edu.utp.filebrowser.Utilites;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Handles uncaught exceptions by displaying native Windows message boxes for error, information, and warning alerts.
 */
public class WindowsGlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    // Unused Class

    // Constants for MessageBox options
    public static final int MB_OK = 0x0;
    public static final int MB_OKCANCEL = 0x1;
    public static final int MB_YESNO = 0x4;
    public static final int MB_ICONINFORMATION = 0x40;
    public static final int MB_ICONWARNING = 0x30;
    public static final int MB_ICONERROR = 0x10;
    public static final int MB_ICONQUESTION = 0x20;
    public static final int MB_YESNOCANCEL = 0x3;
    public static final int MB_SYSTEMMODAL = 0x1000;
    public static final int MB_APPMODAL = 0x0;

    public static final int IDOK = 1;
    public static final int IDCANCEL = 2;
    public static final int IDABORT = 3;
    public static final int IDRETRY = 4;
    public static final int IDIGNORE = 5;
    public static final int IDYES = 6;
    public static final int IDNO = 7;

    /**
     * Interface to interact with user32.dll for displaying MessageBox on Windows.
     */
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);
        int MessageBoxW(WinDef.HWND hWnd, WString text, WString caption, int uType);
    }

    /**
     * Handles uncaught exceptions by showing an error message box with the final cause message.
     *
     * @param t the thread where the exception occurred
     * @param e the exception that was thrown
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Throwable finalCause = getFinalCause(e);
        Platform.runLater(() -> alertError("Error", finalCause.getMessage()));
    }

    /**
     * Retrieves the native handle of the main window.
     *
     * @return the HWND of the main window
     */
    private static WinDef.HWND getMainWindowHandle() {
        long hwnd = com.sun.glass.ui.Window.getWindows().stream()
                .filter(window -> window.getView() != null)
                .map(window -> window.getView().getNativeView())
                .findFirst()
                .orElse(0L);
        return new WinDef.HWND(new Pointer(hwnd));
    }

    /**
     * Retrieves the primary stage of the JavaFX application.
     *
     * @return the primary Stage object
     */
    private static Stage getPrimaryStage() {
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage) {
                return (Stage) window;
            }
        }
        return null;
    }

    /**
     * Displays an error message box with the specified title and context text.
     *
     * @param title       the title of the message box
     * @param contextText the content text of the message box
     */
    public static void alertError(String title, String contextText){
        User32.INSTANCE.MessageBoxW(
                        null,
                        new WString(contextText),
                        new WString(title),
                        MB_OK | MB_ICONERROR | MB_SYSTEMMODAL | MB_APPMODAL
                        );
    }

    /**
     * Recursively retrieves the final cause of an exception.
     *
     * @param e the initial exception
     * @return the final cause of the exception
     */
    private static Throwable getFinalCause(Throwable e) {
        if(e.getCause() == null) return e;
        return getFinalCause(e.getCause());
    }

    /**
     * Displays an information message box with the specified title and context text.
     *
     * @param title       the title of the message box
     * @param contextText the content text of the message box
     */
    public static void alertInformation(String title, String contextText){
        User32.INSTANCE.MessageBoxW(
                null,
                new WString(contextText),
                new WString(title),
                MB_OK | MB_ICONINFORMATION | MB_SYSTEMMODAL | MB_APPMODAL
        );
    }

    /**
     * Displays a warning message box with the specified title and context text.
     *
     * @param title       the title of the message box
     * @param contextText the content text of the message box
     */
    public static void alertWarning(String title, String contextText){
        User32.INSTANCE.MessageBoxW(
                null,
                new WString(contextText),
                new WString(title),
                MB_OK | MB_ICONWARNING | MB_SYSTEMMODAL | MB_APPMODAL
        );
    }
}
