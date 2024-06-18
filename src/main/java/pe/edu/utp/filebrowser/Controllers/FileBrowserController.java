package pe.edu.utp.filebrowser.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.utp.filebrowser.DSA.HashMap;
import pe.edu.utp.filebrowser.DSA.DynamicStack;
import pe.edu.utp.filebrowser.DSA.Tree;
import pe.edu.utp.filebrowser.FileBrowser;
import pe.edu.utp.filebrowser.FileSystem.*;
import pe.edu.utp.filebrowser.TreeAndTable.CellFactory;
import pe.edu.utp.filebrowser.TreeAndTable.RootItem;
import pe.edu.utp.filebrowser.Utilites.ConfirmationOptions;
import pe.edu.utp.filebrowser.Utilites.GlobalExceptionHandler;
import pe.edu.utp.filebrowser.Utilites.Section;

import static pe.edu.utp.filebrowser.TreeAndTable.CellUtilityManager.handleSelectedCellPressed;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FileBrowserController {

    // FXML tags
    // TreeItem
    private final TreeItem<FileEntity> rootItemMP = new TreeItem<>(new RootItem("My PC", FileTypes.PC));
    private final TreeItem<FileEntity> rootItemDA = new TreeItem<>(new RootItem("My Direct Access", FileTypes.DIRECTACCESS));

    // MenuBar
    @FXML
    private MenuBar menuBar;

    // TreeView
    @FXML
    private TreeView<FileEntity> treeViewDA = new TreeView<>();
    @FXML
    private TreeView<FileEntity> treeViewMP = new TreeView<>();

    // TableView
    @FXML
    private TableView<FileEntity> tableView;

    // TableColumn
    @FXML
    private TableColumn<FileEntity, FileEntity> tableColumnName;
    @FXML
    private TableColumn<FileEntity, String> tableColumnModDate, tableColumnType, tableColumnCreationDate;
    @FXML
    private TableColumn<FileEntity, String> tableColumnPath, tableColumnSize;

    // TextField
    @FXML
    private TextField textField_inputPath, textField_search, textFieldEditorTitle;

    // TextArea
    @FXML
    private TextArea textAreaEditorText;

    // Pane's
    @FXML
    private Pane paneTextEditor, paneTableView;

    // Buttons
    @FXML
    private Button buttonBackwardPath, buttonForwardPath;

    // JavaFX element's without @FXML tag
    // JavaFX ContextMenus
    private ContextMenu contextMenuEditName;

    // FileEntity
    private PlainText selectedFilePT;

    // Controllers
    private EntryNameController entryNameController;

    // Stage
    private final Stage stageEntryName = new Stage();
    private Stage primaryStage;


    // DSA
    // Tree
    private final Tree fileTree = new Tree();

    // HashMap
    private final HashMap<Path, TreeItem<FileEntity>> fileAssignmentTable = new HashMap<>();

    // Stack
    private final DynamicStack<Path> BackwardPathStack = new DynamicStack<>();
    private final DynamicStack<Path> ForwardPathStack = new DynamicStack<>();

    // Others
    // Booleans
    private boolean isDeselectionByUser = true;

    // Path
    private Path pathIsSelected = Path.of("\\");


    public void initialize() {
        setupTableView();
        setupTreeView();
        createSubWindows();
    }

    private void setupTableView(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        tableColumnName.setCellFactory(_ -> CellFactory.setCellFactoryTable());
        tableColumnModDate.setCellFactory(_ -> CellFactory.setCellFactoryTable());
        tableColumnType.setCellFactory(_ -> CellFactory.setCellFactoryTable());
        tableColumnCreationDate.setCellFactory(_ -> CellFactory.setCellFactoryTable());
        tableColumnPath.setCellFactory(_ -> CellFactory.setCellFactoryTable());
        tableColumnSize.setCellFactory(_ -> CellFactory.setCellFactoryTable());
        tableColumnName.setReorderable(false);
        tableColumnModDate.setReorderable(false);
        tableColumnType.setReorderable(false);
        tableColumnCreationDate.setReorderable(false);
        tableColumnPath.setReorderable(false);
        tableColumnSize.setReorderable(false);

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

        tableColumnCreationDate.setCellValueFactory(param -> {
            FileEntity row = param.getValue();
            if (row == null) return null;
            return new javafx.beans.property
                    .SimpleStringProperty(row.getCreationDate().format(formatter));
        });

        tableColumnPath.setCellValueFactory(param -> {
            FileEntity row = param.getValue();
            if (row == null) return null;
            return new javafx.beans.property
                    .SimpleStringProperty(
                    (row.getDirectoryPath() == null) ? "\\"
                            : Path.of("\\").resolve(row.getDirectoryPath()).toString());
        });

        tableColumnSize.setCellValueFactory(param -> {
            FileEntity row = param.getValue();
            String result = "";
            if (row == null) return null;

            if (row instanceof PlainText) result = row.getSize()+" Bytes";
            else if (row instanceof DirectAccess)
                if (((DirectAccess) row).getTargetFile() instanceof PlainText)
                    result = row.getSize()+" Bytes";

            return new javafx.beans.property
                    .SimpleStringProperty(result);
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((_) -> {
            if(isDeselectionByUser) deselectListCell(Section.TREEVIEW);
        });

        tableView.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                if(!handleSelectedCellPressed(tableView, event)){
                    deselectListCell(Section.TABLEVIEW);
                    return;
                }
                FileEntity item = tableView.getSelectionModel().getSelectedItem();

                if(item instanceof DirectAccess)
                    item = ((DirectAccess) item).getTargetFile();

                if(item instanceof PlainText) {
                    openTextEditor(item);
                    return;
                }

                gotToDirectory(item.getPath());
            }
        });
    }

    private void setupTreeView(){
        treeViewMP.setCellFactory((_) -> CellFactory.setCellFactoryTree());
        treeViewMP.setRoot(rootItemMP);
        treeViewDA.setCellFactory((_) -> CellFactory.setCellFactoryTree());
        treeViewDA.setRoot(rootItemDA);

        treeViewDA.getSelectionModel().selectedItemProperty().addListener((_) -> {
            if(isDeselectionByUser) deselectListCell(Section.TREEVIEW_MYFILES, Section.TABLEVIEW);
        });

        treeViewMP.getSelectionModel().selectedItemProperty().addListener((_) -> {
            if(isDeselectionByUser) deselectListCell(Section.TREEVIEW_DIRECTACCESS, Section.TABLEVIEW);
        });

        treeViewMP.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                if(!handleSelectedCellPressed(treeViewMP, event)){
                    deselectListCell(Section.TREEVIEW_MYFILES);
                    return;
                }

                TreeItem<FileEntity> item = treeViewMP.getSelectionModel().getSelectedItem();
                FileEntity itemValue = item.getValue();

                if(Objects.equals(item, rootItemMP)){
                    gotToDirectory(rootItemMP.getValue().getDirectoryPath());
                    return;
                }

                if(itemValue instanceof DirectAccess)
                    itemValue = ((DirectAccess) itemValue).getTargetFile();

                if (itemValue instanceof PlainText) {
                    openTextEditor(itemValue);
                    return;
                }

                gotToDirectory(itemValue.getPath());
            }
        });

        treeViewDA.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                if(!handleSelectedCellPressed(treeViewDA, event)){
                    deselectListCell(Section.TREEVIEW_DIRECTACCESS);
                    return;
                }

                TreeItem<FileEntity> item = treeViewDA.getSelectionModel().getSelectedItem();
                FileEntity itemValue = item.getValue();

                if(Objects.equals(rootItemDA, item)) return;

                if(itemValue instanceof DirectAccess)
                    itemValue = ((DirectAccess) itemValue).getTargetFile();

                if (itemValue instanceof PlainText) {
                    openTextEditor(itemValue);
                    return;
                }

                gotToDirectory(itemValue.getPath());
            }
        });

    }

    //

    private void gotToDirectory(Path target){
        // Path.of("\\") -> root
        if(target == null) return;

        if(Objects.equals(target, pathIsSelected)) return;

        if(!target.equals(Path.of("\\")) && !fileAssignmentTable.contains(target)){
            textField_inputPath.clear();
            textField_inputPath.setText(generateValidPath(pathIsSelected.toString())); // check
            // alert
            return;
        }

        BackwardPathStack.push(pathIsSelected);
        ForwardPathStack.clear();

        pathIsSelected = target;

        buttonBackwardPath.setDisable(false);
        buttonForwardPath.setDisable(true);

        textField_inputPath.clear();
        textField_inputPath.setText(generateValidPath(pathIsSelected.toString()));

        tableView.getItems().clear();
        tableView.getItems().addAll(fileTree.getFilesEntitiesInDirectory(pathIsSelected));
    }

    private void createSubWindows(){
        FXMLLoader loaderEntryName = new FXMLLoader(FileBrowser.class.getResource("Input.fxml"));
        try{
            // creates the EntryName subwindow
            Scene sceneEntryName = new Scene(loaderEntryName.load(), 309, 71);
            sceneEntryName.getStylesheets().add(Objects.requireNonNull(FileBrowser.class.getResource("style/style.css")).toExternalForm());
            stageEntryName.setTitle("Entry Name");
            stageEntryName.setScene(sceneEntryName);
            stageEntryName.setResizable(false);
            entryNameController = loaderEntryName.getController();
            stageEntryName.setOnCloseRequest(_ -> entryNameController.setName(""));
            stageEntryName.initModality(Modality.APPLICATION_MODAL);
            stageEntryName.initOwner(primaryStage);

        } catch (IOException e){
            GlobalExceptionHandler.alertWarning(
                    "Error",
                    "An error ocurred",
                    "Error loading sub windows"
            );
            Platform.exit();
        }

    }

    private void runSubWindow(String entryName, String title){
        entryNameController.setName(entryName);
        entryNameController.setTitle(title);
        stageEntryName.showAndWait();
    }

    private boolean createFile(FileEntity fe, TreeItem<FileEntity> treeItemParent){
        if(!fileTree.push(fe)) return false;
        TreeItem<FileEntity> currentFileTI = new TreeItem<>(fe);
        treeItemParent.getChildren().add(currentFileTI);
        fileAssignmentTable.put(fe.getPath(), currentFileTI);
        tableView.getItems().clear();
        tableView.getItems().addAll(fileTree.getChildFilesEntities(fe));
        return true;
    }

    @FXML
    private void createVirtualDisk(){
        runSubWindow("", "Create Virtual Disk");
        String name = entryNameController.getName();
        if(name.isEmpty() || name.isBlank())
            return;
        VirtualDiskDriver vdd = new VirtualDiskDriver(name, rootItemMP.getValue().getDirectoryPath());
        createFile(vdd, rootItemMP);
    }

    @FXML
    private void createFolder(){
        runSubWindow("", "Create Folder");
        String name = entryNameController.getName();
        if(name.isEmpty() || name.isBlank()) return;
        if(pathIsSelected.equals(Path.of("\\"))) return;
        Folder folder = new Folder(name, pathIsSelected);
        TreeItem<FileEntity> treeItemParent = fileAssignmentTable.get(folder.getDirectoryPath());
        createFile(folder, treeItemParent);
    }

    @FXML
    private void createPlainText(){
        runSubWindow("", "Create PlainText");
        String name = entryNameController.getName();
        if(name.isEmpty() || name.isBlank()) return;
        if(pathIsSelected.equals(Path.of("\\"))) return;
        PlainText plainText = new PlainText(name, pathIsSelected);
        TreeItem<FileEntity> treeItemParent = fileAssignmentTable.get(plainText.getDirectoryPath());
        createFile(plainText, treeItemParent);
    }

    @FXML
    private void createShortCut(){
        if(pathIsSelected.equals(Path.of("\\"))) return;
        runSubWindow("", "Create ShortCut");
        FileEntity targetFile = tableView.getSelectionModel().getSelectedItem();
        String name = entryNameController.getName();
        if(name.isEmpty() || name.isBlank()) name = targetFile.getName();
        name += " - Direct Access";

        DirectAccess directAccess = new DirectAccess(name, pathIsSelected, targetFile);
        TreeItem<FileEntity> treeItemParent = fileAssignmentTable.get(directAccess.getShortcutDirectoryPath());
        TreeItem<FileEntity> currentFileTI1 = new TreeItem<>(directAccess);
        if(createFile(directAccess, treeItemParent))
            rootItemDA.getChildren().add(currentFileTI1);
    }

    @FXML
    private void backwardPath(){
        if(BackwardPathStack.isEmpty()) return;
        ForwardPathStack.push(pathIsSelected);
        pathIsSelected = BackwardPathStack.pop();
        if(BackwardPathStack.isEmpty()) buttonBackwardPath.setDisable(true);
        buttonForwardPath.setDisable(false);
        textField_inputPath.clear();
        textField_inputPath.setText(generateValidPath(pathIsSelected.toString()));
        tableView.getItems().clear();
        tableView.getItems().addAll(fileTree.getFilesEntitiesInDirectory(pathIsSelected));
    }

    @FXML
    private void forwardPath(){
        if(ForwardPathStack.isEmpty()) return;
        BackwardPathStack.push(pathIsSelected);
        pathIsSelected = ForwardPathStack.pop();
        if(ForwardPathStack.isEmpty()) buttonForwardPath.setDisable(true);
        buttonBackwardPath.setDisable(false);
        textField_inputPath.clear();
        textField_inputPath.setText(generateValidPath(pathIsSelected.toString()));
        tableView.getItems().clear();
        tableView.getItems().addAll(fileTree.getFilesEntitiesInDirectory(pathIsSelected));
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
    }

    private void openTextEditor(FileEntity fileEntity){
        selectedFilePT = (PlainText) fileEntity;
        textFieldEditorTitle.setText(selectedFilePT.getName()+" - Plain Text");
        textAreaEditorText.setText(selectedFilePT.getContent());
        buttonForwardPath.setDisable(true);
        buttonBackwardPath.setDisable(true);
        textField_inputPath.setDisable(true);
        textField_search.setDisable(true);
        treeViewDA.setDisable(true);
        treeViewMP.setDisable(true);
        paneTableView.setVisible(false);
        paneTextEditor.setVisible(true);

    }

    @FXML
    private void saveTextEditorContent(){
        if((selectedFilePT.getContent().equals(textAreaEditorText.getText())))
           return;
        selectedFilePT.setContent(textAreaEditorText.getText());
        GlobalExceptionHandler.alertInformation(
                "Save",
                "Save content!",
                "The content of the file was saved!");
    }

    @FXML
    private void closeTextEditor(){
        if(!(selectedFilePT.getContent().equals(textAreaEditorText.getText()))) {
            ConfirmationOptions co = GlobalExceptionHandler.alertConfirmation(
                    "Close",
                    "Unsaved content",
                    "Do you wish to save changes?"
            );
            if(co == ConfirmationOptions.YES) saveTextEditorContent();
            else if (co == ConfirmationOptions.CANCEL) return;
        }
        selectedFilePT = null;
        textAreaEditorText.clear();
        textFieldEditorTitle.clear();
        paneTextEditor.setVisible(false);
        paneTableView.setVisible(true);
        buttonForwardPath.setDisable(true);
        buttonBackwardPath.setDisable(false);
        textField_inputPath.setDisable(false);
        textField_search.setDisable(false);
        treeViewDA.setDisable(false);
        treeViewMP.setDisable(false);
        tableView.refresh();
    }

    @FXML
    private void search(){
        textField_search.getText();
    }

    private String generateValidPath(String path) {
        if (path == null || path.isEmpty() || path.isBlank())
            return "\\";

        if (path.startsWith("\\") || path.startsWith(" "))
            path = path.substring(1);

        if (path.endsWith("\\") || path.endsWith(" "))
            path = path.substring(0, path.length() - 1);

        if((!path.startsWith("\\") && !path.startsWith(" "))
                && (!path.endsWith("\\") && !path.endsWith(" ")))
            return "\\"+path;

        return generateValidPath(path);

    }

    private Path generateValidPath(Path path){
        String auxPath = generateValidPath(path.toString());
        return Path.of(auxPath);
    }

    @FXML
    private void navigateToDirectory(){
        String inputPath = textField_inputPath.getText();
        Path path = generateValidPath(Path.of(inputPath));
        if(Objects.equals(path, BackwardPathStack.peek())) backwardPath();
        else gotToDirectory(path);
    }

    @FXML
    public void quit(){
        Platform.exit();
    }

    public void setStage(Stage stage){
        this.primaryStage = stage;
    }

    @SuppressWarnings("ClassEscapesDefinedScope")
    public void deselectListCell(Section...sections) {
        isDeselectionByUser = false;
        for(Section section: sections)
            switch (section) {
                case TABLEVIEW:
                    tableView.getSelectionModel().clearSelection();
                    break;
                case TREEVIEW:
                    treeViewDA.getSelectionModel().clearSelection();
                    treeViewMP.getSelectionModel().clearSelection();
                    break;
                case TREEVIEW_MYFILES:
                    treeViewMP.getSelectionModel().clearSelection();
                    break;
                case TREEVIEW_DIRECTACCESS:
                    treeViewDA.getSelectionModel().clearSelection();
                    break;
                case ALL:
                    tableView.getSelectionModel().clearSelection();
                    treeViewDA.getSelectionModel().clearSelection();
                    treeViewMP.getSelectionModel().clearSelection();
                    break;
            }
        isDeselectionByUser = true;
    }



}