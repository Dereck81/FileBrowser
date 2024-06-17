package pe.edu.utp.filebrowser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import pe.edu.utp.filebrowser.Controllers.FileBrowserController;
import pe.edu.utp.filebrowser.Utilites.Section;

import java.io.IOException;
import java.util.Objects;

public class FileBrowser extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FileBrowser.class.getResource("FileBrowser.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1077, 597);
        FileBrowserController controller = fxmlLoader.getController();
        scene.getStylesheets().add(Objects
                .requireNonNull(getClass().getResource("style/style.css")).toExternalForm());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.getCode().getName().equalsIgnoreCase("ESC")) {
                controller.deselectListCell(Section.ALL);
            }
        });
        stage.setOnCloseRequest(_ -> controller.quit());
        stage.setTitle("File Browser");
        stage.setScene(scene);
        stage.setResizable(false);
        controller.setStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}