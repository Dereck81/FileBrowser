package pe.edu.utp.filebrowser.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import pe.edu.utp.filebrowser.FileSystem.FileEntity;

public class FileBrowserController {

    // FXML tags

    // TreeView
    @FXML
    private TreeView<FileEntity> treeView;

    // TableView
    @FXML
    private TableView<FileEntity> tableView;

    // TextField
    @FXML
    private TextField textField_inputPath;
    @FXML
    private TextField textField_search;


    public void initialize() {

    }


}