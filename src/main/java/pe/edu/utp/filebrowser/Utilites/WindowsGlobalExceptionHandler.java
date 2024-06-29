package pe.edu.utp.filebrowser.Utilites;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;

public class WindowsGlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    // Unused Class

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


    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);
        int MessageBoxW(WinDef.HWND hWnd, WString text, WString caption, int uType);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Throwable finalCause = getFinalCause(e);
        Platform.runLater(() -> alertError("Error", finalCause.getMessage()));
    }

    private static WinDef.HWND getMainWindowHandle() {
        long hwnd = com.sun.glass.ui.Window.getWindows().stream()
                .filter(window -> window.getView() != null)
                .map(window -> window.getView().getNativeView())
                .findFirst()
                .orElse(0L);
        return new WinDef.HWND(new Pointer(hwnd));
    }

    private static Stage getPrimaryStage() {
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage) {
                return (Stage) window;
            }
        }
        return null;
    }

    public static void alertError(String title, String contextText){
        User32.INSTANCE.MessageBoxW(
                        null,
                        new WString(contextText),
                        new WString(title),
                        MB_OK | MB_ICONERROR | MB_SYSTEMMODAL | MB_APPMODAL
                        );
    }

    private static Throwable getFinalCause(Throwable e) {
        if(e.getCause() == null) return e;
        return getFinalCause(e.getCause());
    }

    public static void alertInformation(String title, String contextText){
        User32.INSTANCE.MessageBoxW(
                null,
                new WString(contextText),
                new WString(title),
                MB_OK | MB_ICONINFORMATION | MB_SYSTEMMODAL | MB_APPMODAL
        );
    }

    public static void alertWarning(String title, String contextText){
        User32.INSTANCE.MessageBoxW(
                null,
                new WString(contextText),
                new WString(title),
                MB_OK | MB_ICONWARNING | MB_SYSTEMMODAL | MB_APPMODAL
        );
    }



}
