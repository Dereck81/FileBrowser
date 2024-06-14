package pe.edu.utp.filebrowser.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import pe.edu.utp.filebrowser.DSA.Tree;
import pe.edu.utp.filebrowser.FileSystem.*;
import pe.edu.utp.filebrowser.TreeAndTable.CellFactory;
import pe.edu.utp.filebrowser.TreeAndTable.RootItem;
import pe.edu.utp.filebrowser.Utilites.ConfirmationOptions;
import pe.edu.utp.filebrowser.Utilites.GlobalExceptionHandler;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

public class FileBrowserController {

    // FXML tags

    // TreeItem
    private final TreeItem<FileEntity> rootItemMF = new TreeItem<>(new RootItem("My Files"));
    private final TreeItem<FileEntity> rootItemDA = new TreeItem<>(new RootItem("My Direct Access"));
    // TreeView
    @FXML
    private TreeView<FileEntity> treeViewDA = new TreeView<>();
    @FXML
    private TreeView<FileEntity> treeViewMF = new TreeView<>();

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

    // TextArea
    @FXML
    private TextArea textAreaEditorText;

    // Pane's
    @FXML
    private Pane paneTextEditor, paneTableView;

    // JavaFX element's without @FXML tag
    // JavaFX ContextMenus
    private ContextMenu contextMenuEditName;

    // Others
    // FileEntity
    private FileEntity selectedFilePT;

    // Tree
    private Tree tree = new Tree();

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
        treeViewMF.setCellFactory((_) -> CellFactory.setCellFactoryTree());
        treeViewMF.setRoot(rootItemMF);
        treeViewDA.setCellFactory((_) -> CellFactory.setCellFactoryTree());
        treeViewDA.setRoot(rootItemDA);

    }

    @FXML
    private void createVirtualDisk(){
        VirtualDiskDriver vdd = new VirtualDiskDriver("");
        if(!tree.add(vdd)) return;
        rootItemMF.getChildren().add(new TreeItem<>(vdd));
        tableView.getItems().setAll(tree.getFilesEntityFromFile(vdd));
        tableView.refresh();
    }


    @FXML
    private void convertToPhysicalDirectory(){
       ConfirmationOptions co = GlobalExceptionHandler.alertConfirmation(
               "Create file directory on hard drive",
               "Do you wish to continue?", "This action can not be undone.");
       if(co != ConfirmationOptions.YES) return;

    }

    @FXML
    protected void test(){
        VirtualDiskDriver c = new VirtualDiskDriver("Disk C");
        VirtualDiskDriver d = new VirtualDiskDriver("Disk D");
        System.out.println(tree.add(c));
        System.out.println(tree.add(d));
        rootItemMF.getChildren().add(new TreeItem<>(c));
        rootItemDA.getChildren().add(new TreeItem<>(d));
        Folder f = new Folder("caca", Path.of(c.getName()));
        if(tree.add(f)) System.out.println("Se agregó");
        System.out.println(f.getDirectoryPath());
        System.out.println(tree.getFileEntityFromPath(f.getPath()));
    }

    private void openTextEditor(){
        if(selectedFilePT == null || !(selectedFilePT instanceof PlainText)) return;
        textAreaEditorText.setText(((PlainText) selectedFilePT).getContent());
        paneTableView.setVisible(false);
        paneTextEditor.setVisible(true);
    }

    @FXML
    private void saveTextEditorContent(){
        if(selectedFilePT == null || !(selectedFilePT instanceof PlainText)) return;
        ((PlainText) selectedFilePT).setContent(textAreaEditorText.getText());
        GlobalExceptionHandler.alertInformation(
                "Save",
                "Save content!",
                "The content of the file was saved!");
    }

    @FXML
    private void closeTextEditor(){
        selectedFilePT = null;
        paneTextEditor.setVisible(false);
        paneTableView.setVisible(true);
    }

    @FXML
    public void quit(){
        Platform.exit();
    }





}