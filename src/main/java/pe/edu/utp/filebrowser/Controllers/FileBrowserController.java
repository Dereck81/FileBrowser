package pe.edu.utp.filebrowser.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.utp.filebrowser.FileSystem.*;
import pe.edu.utp.filebrowser.TreeAndTable.CellFactory;
import pe.edu.utp.filebrowser.TreeAndTable.RootItem;

import java.time.format.DateTimeFormatter;

public class FileBrowserController {

    // FXML tags

    // TreeItem
    private final TreeItem<FileEntity> rootItem = new TreeItem<>(new RootItem("My Files"));

    // TreeView
    @FXML
    private TreeView<FileEntity> treeView = new TreeView<>();

    // TableView
    @FXML
    private TableView<FileEntity> tableView;

    // TableColumn
    @FXML
    private TableColumn<FileEntity, FileEntity> tableColumnName;
    @FXML
    private TableColumn<FileEntity, String> tableColumnModDate, tableColumnType;

    // TextField
    @FXML
    private TextField textField_inputPath;
    @FXML
    private TextField textField_search;

    // JavaFX element's without @FXML tag
    // JavaFX ContextMenus
    private ContextMenu contextMenuEditName;


    public void initialize() {
        setupTableView();
        setupTreeView();


    }

    private void setupTableView(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        tableColumnName.setCellFactory(_ -> CellFactory.setCellFactoryTable());
        tableColumnModDate.setCellFactory(_ -> CellFactory.setCellFactoryTable());
        tableColumnType.setCellFactory(_ -> CellFactory.setCellFactoryTable());
        tableColumnName.setReorderable(false);
        tableColumnModDate.setReorderable(false);
        tableColumnType.setReorderable(false);
        tableColumnName.setCellValueFactory(param -> {
            FileEntity row = param.getValue();
            if (row == null) return null;
            return new javafx.beans.property.SimpleObjectProperty<>(row);
        });
        tableColumnModDate.setCellValueFactory(param -> {
            FileEntity row = param.getValue();
            if (row == null) return null;
            return new javafx.beans.property
                    .SimpleStringProperty(row.getModificationDate().format(formatter));
        });
        tableColumnType.setCellValueFactory(param -> {
            FileEntity row = param.getValue();
            if (row == null) return null;
            return new javafx.beans.property
                    .SimpleStringProperty(row.getFileType().toString());
        });
    }

    private void setupTreeView(){
        treeView.setCellFactory((_) -> CellFactory.setCellFactoryTree());
        treeView.setRoot(rootItem);
    }

    @FXML
    protected void test(){
        TreeItem<FileEntity> item = new TreeItem<>(new VirtualDiskDriver("Disk C"));
        rootItem.getChildren().add(item);
        item.getChildren().add(new TreeItem<>(new Folder("Proyect", "/")));
        tableView.getItems().add(new Folder("Proyect", "/"));

    }



}