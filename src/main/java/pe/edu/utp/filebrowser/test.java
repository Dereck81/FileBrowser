package pe.edu.utp.filebrowser;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

public class test extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear un TreeItem con un título
        TreeItem<String> rootItem = new TreeItem<>("Root");

        // Agregar hijos al TreeItem
        TreeItem<String> childItem1 = new TreeItem<>("Child 1");
        TreeItem<String> childItem2 = new TreeItem<>("Child 2");
        rootItem.getChildren().addAll(childItem1, childItem2);

        // Crear un TreeView y establecer la raíz
        TreeView<String> treeView = new TreeView<>(rootItem);

        // Configurar la escena y mostrarla
        Scene scene = new Scene(treeView, 200, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}