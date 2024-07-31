package pe.edu.utp.filebrowser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import pe.edu.utp.filebrowser.Controllers.FileBrowserController;
import pe.edu.utp.filebrowser.Enums.Section;
import pe.edu.utp.filebrowser.Utilites.JavaFXGlobalExceptionHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class FileBrowser extends Application {
    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        Thread.setDefaultUncaughtExceptionHandler(new JavaFXGlobalExceptionHandler());

        FXMLLoader fxmlLoader = new FXMLLoader(FileBrowser.class.getResource("FileBrowser.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1077, 620);
        Image icon = new Image(String.valueOf(Objects.requireNonNull(FileBrowser.class.getResource("imgs/Application.png")).toURI()));
        FileBrowserController controller = fxmlLoader.getController();

        KeyCombination copy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        KeyCombination cut = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);
        KeyCombination paste = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
        KeyCombination save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        KeyCombination open = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        KeyCombination createFileDirectoryHardDisk = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);

        scene.getStylesheets().add(Objects
                .requireNonNull(getClass().getResource("style/style.css")).toExternalForm());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.getCode().getName().equalsIgnoreCase("ESC")) {
                controller.deselectListCell(Section.ALL);
            }

            if(copy.match(keyEvent)){
                controller.copyKeyCombination();
                keyEvent.consume();

            } else if (paste.match(keyEvent)) {
                controller.pasteKeyCombination();
                keyEvent.consume();

            } else if (cut.match(keyEvent)) {
                controller.cutKeyCombination();
                keyEvent.consume();

            } else if (save.match(keyEvent)) {
                controller.saveFileKeyCombination();
                keyEvent.consume();

            } else if (open.match(keyEvent)){
                controller.openKeyCombination();
                keyEvent.consume();

            } else if (createFileDirectoryHardDisk.match(keyEvent)){
                try {
                    controller.createFileHardDiskKC();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                keyEvent.consume();

            }
            //System.out.println(keyEvent.getCode().getName());
        });

        stage.setOnCloseRequest(_ -> controller.quit());
        stage.setTitle("File Browser");
        stage.setScene(scene);
        stage.getIcons().add(icon);
        stage.setResizable(false);

        controller.setStage(stage);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}