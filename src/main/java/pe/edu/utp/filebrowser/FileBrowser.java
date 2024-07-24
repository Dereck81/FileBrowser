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

        KeyCombination copy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
        KeyCombination cut = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_ANY);
        KeyCombination paste = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY);
        KeyCombination createFileDirectoryHardDisk = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_ANY);

        scene.getStylesheets().add(Objects
                .requireNonNull(getClass().getResource("style/style.css")).toExternalForm());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.getCode().getName().equalsIgnoreCase("ESC")) {
                controller.deselectListCell(Section.ALL);
            }

            if(copy.match(keyEvent)){

                keyEvent.consume();
            } else if (paste.match(keyEvent)) {

                keyEvent.consume();
            } else if (cut.match(keyEvent)){

                keyEvent.consume();
            } else if (createFileDirectoryHardDisk.match(keyEvent)){

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