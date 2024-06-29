package pe.edu.utp.filebrowser.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.utp.filebrowser.DSA.HashMap;
import pe.edu.utp.filebrowser.DSA.DynamicStack;
import pe.edu.utp.filebrowser.DSA.FSTree;
import pe.edu.utp.filebrowser.FileBrowser;
import pe.edu.utp.filebrowser.FileSystem.*;
import pe.edu.utp.filebrowser.IO.IO;
import pe.edu.utp.filebrowser.IO.ObjectSerializationUtil;
import pe.edu.utp.filebrowser.TreeAndTable.CellFactory;
import pe.edu.utp.filebrowser.FileSystem.RootItem;
import pe.edu.utp.filebrowser.Enums.ConfirmationOptions;
import pe.edu.utp.filebrowser.Utilites.JavaFXGlobalExceptionHandler;
import pe.edu.utp.filebrowser.Enums.Section;
import pe.edu.utp.filebrowser.Enums.FileTypes;

import static pe.edu.utp.filebrowser.TreeAndTable.CellUtilityManager.handleSelectedCellPressed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import pe.edu.utp.filebrowser.FileSystem.Path;
import pe.edu.utp.filebrowser.Utilites.JavaFXNotifications;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class FileBrowserController {

    // FXML tags
    // TreeItem
    private final TreeItem<FileEntity> rootItemMP = new TreeItem<>(new RootItem("My PC", FileTypes.PC));
    private final TreeItem<FileEntity> rootItemDA = new TreeItem<>(new RootItem("My Direct Access", FileTypes.DIRECTACCESS));

    // MenuBar
    @FXML
    private MenuBar menuBar;

    // Menu
    @FXML
    private Menu menuOpenRecentFile;

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
    private Pane paneTextEditor, paneTableView, paneInfo;

    // Label
    @FXML
    private Label labelAction, labelFileName, labelPathFileName;

    // Buttons
    @FXML
    private Button buttonBackwardPath, buttonForwardPath;

    // JavaFX element's without @FXML tag
    // JavaFX ContextMenus
    private final ContextMenu contextMenuTableV = new ContextMenu();
    private final ContextMenu contextMenuTreeMP = new ContextMenu();
    private final ContextMenu contextMenuTreeDA = new ContextMenu();
    private final ContextMenu contextMenuFO = new ContextMenu();

    // File Chooser
    private FileChooser fileChooserSaveFile, fileChooserOpen;

    // File
    private File file;

    // FileEntity
    private PlainText selectedFilePT;

    // Controllers
    private EntryNameController entryNameController;

    // Stage
    private final Stage stageEntryName = new Stage();
    private Stage primaryStage;


    // DSA
    // Tree
    private final FSTree fileFSTree = new FSTree();

    // HashMap
    private final HashMap<Path, TreeItem<FileEntity>> fileAssignmentTable = new HashMap<>();

    // Stack
    private final DynamicStack<Path> BackwardPathStack = new DynamicStack<>();
    private final DynamicStack<Path> ForwardPathStack = new DynamicStack<>();
    private final DynamicStack<FileEntity> fileTransferStack = new DynamicStack<>();

    // Others
    // Booleans
    private boolean isDeselectionByUser = true;

    // Path
    private Path pathIsSelected = new Path(Path.separatorToUse);
    private final Path rootPath = new Path(Path.separatorToUse);


    public void initialize() {
        setupContextMenu();
        setupFileChoosers();
        setupTableView();
        setupTreeView();
        createSubWindows();
    }

    private void setupFileChoosers(){
        // fileChooserOpen
        fileChooserOpen = new FileChooser();
        fileChooserOpen.setTitle("Open File");
        fileChooserOpen.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SAV", "*.sav")
        );
        // fileChooserSaveFile
        fileChooserSaveFile = new FileChooser();
        fileChooserSaveFile.setTitle("Save File");
        fileChooserSaveFile.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SAV", "*.sav")
        );
    }

    private void setupContextMenu(){
        MenuItem copyPathTableV = new MenuItem("Copy Path");
        copyPathTableV.setOnAction(_ -> {
            FileEntity fe = tableView.getSelectionModel().getSelectedItem();
            copyToClipboard(fe);
        });
        MenuItem copyPathTreeMP = new MenuItem("Copy Path");
        copyPathTreeMP.setOnAction(_ -> {
            FileEntity fe = treeViewMP.getSelectionModel().getSelectedItem().getValue();
            copyToClipboard(fe);
        });
        MenuItem copyPathTreeDA = new MenuItem("Copy Path");
        copyPathTreeDA.setOnAction(_ -> {
            FileEntity fe = treeViewDA.getSelectionModel().getSelectedItem().getValue();
            copyToClipboard(fe);
        });
        MenuItem editNameTableV = new MenuItem("Edit Name");
        editNameTableV.setOnAction(_ -> {
            FileEntity fe = tableView.getSelectionModel().getSelectedItem();
            editName(fe);
        });
        MenuItem editNameTreeMP = new MenuItem("Edit Name");
        editNameTreeMP.setOnAction(_ -> {
            FileEntity fe = treeViewMP.getSelectionModel().getSelectedItem().getValue();
            editName(fe);
        });
        MenuItem editNameTreeDA = new MenuItem("Edit Name");
        editNameTreeDA.setOnAction(_ -> {
            FileEntity fe = treeViewDA.getSelectionModel().getSelectedItem().getValue();
            editName(fe);
        });
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(_ -> {
            FileEntity fe = tableView.getSelectionModel().getSelectedItem();
            copyFileEntity(fe);
        });
        MenuItem cut = new MenuItem("Cut");
        cut.setOnAction(_ -> {
            FileEntity fe = tableView.getSelectionModel().getSelectedItem();
            cutFileEntity(fe);
        });
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(_ -> {
            FileEntity fe = tableView.getSelectionModel().getSelectedItem();
            removeFileEntity(fe);
        });
        // contextMenuFO
        MenuItem createDiskMI = new MenuItem("Create a new virtual disk");
        createDiskMI.setOnAction(_ -> createVirtualDisk());
        MenuItem createFolderMI = new MenuItem("Create a new folder");
        createFolderMI.setOnAction(_ -> createFolder());
        MenuItem createPlainTextMI = new MenuItem("Create a new plaintext");
        createPlainTextMI.setOnAction(_ -> createPlainText());
        MenuItem createShortCutTableV = new MenuItem("Create a new shortcut");
        createShortCutTableV.setOnAction(_ -> {
            FileEntity fe = tableView.getSelectionModel().getSelectedItem();
            if(fe == null) return;
            createShortCut();
        });

        contextMenuTableV.getItems().setAll(createShortCutTableV, editNameTableV, copyPathTableV, copy, cut);
        contextMenuFO.getItems().setAll(createDiskMI, createFolderMI, createPlainTextMI);
        contextMenuTreeDA.getItems().setAll(editNameTreeDA, copyPathTreeDA);
        contextMenuTreeMP.getItems().setAll(editNameTreeMP, copyPathTreeMP);
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
                    .SimpleStringProperty(row.getDirectoryPath().getPath());
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
            }else if (event.getButton() == MouseButton.SECONDARY){
                if(!handleSelectedCellPressed(tableView, event)){
                    tableView.setContextMenu(contextMenuFO);
                    deselectListCell(Section.TABLEVIEW);
                }else tableView.setContextMenu(contextMenuTableV);
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
            }else {
                showContextMenu(event, treeViewMP, rootItemMP, contextMenuTreeMP);
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
            }else showContextMenu(event, treeViewDA, rootItemDA, contextMenuTreeDA);
        });

    }

    private void showContextMenu(MouseEvent event, TreeView<FileEntity> treeView, TreeItem<FileEntity> rootItem, ContextMenu contextMenu) {
        if (event.getButton() == MouseButton.SECONDARY){
            TreeItem<FileEntity> ti = treeView.getSelectionModel().getSelectedItem();
            if(ti == null || ti == rootItem){
                treeView.setContextMenu(null);
                return;
            }
            if(!handleSelectedCellPressed(treeView, event)){
                treeView.setContextMenu(null);
            }else treeView.setContextMenu(contextMenu);
        }
    }

    //

    @FXML
    private void openFile(){
        file = fileChooserOpen.showOpenDialog(null);
        if (file == null) return;
        IO.writeRecordRecentFiles(file.getPath());
        updateRecentFiles();
    }

    @FXML
    private void saveFile() throws Exception {
        JavaFXGlobalExceptionHandler.alertInformation(
                "Save file",
                "Save file",
                "Do you want to save the file?"
        );
        file = fileChooserSaveFile.showSaveDialog(null);
        if (file == null) return;
        if(ObjectSerializationUtil.serialize(fileAssignmentTable, fileFSTree, file.getPath()))
            JavaFXGlobalExceptionHandler.alertInformation(
                    "Information",
                    "Information about the saved file",
                    "the file was exported successfully");
    }

    @FXML
    private void closeFile(){

    }

    /**
     * Opens a recently accessed file based on the selected menu item.
     * @param pathRecentFile The path of the recently accessed file.
     */
    private void openRecentFile(String pathRecentFile) throws FileNotFoundException {
        file = new File(pathRecentFile);
        if (!file.exists()){
            IO.deleteRecentFilesRecord(pathRecentFile);
            updateRecentFiles();
            throw new FileNotFoundException("File not found: "+pathRecentFile);
        }
    }

    /**
     * Updates the list of recent files in the menu.
     */
    private void updateRecentFiles(){
        String[] directorySeparators = new String[]{"/", "\\"};
        ArrayList<String> recentFiles = IO.readRecordRecentFiles();
        int index = 0;

        if (menuOpenRecentFile != null) menuOpenRecentFile.getItems().clear();

        for (String pathRecentFile : recentFiles.toArray(new String[0])){
            for (String ds: directorySeparators){
                index = pathRecentFile.lastIndexOf(ds);
                if(index != -1)
                    break;
            }
            String nameFile = pathRecentFile.substring(index+1);
            MenuItem menuItemRecentFile = new MenuItem(nameFile);
            menuItemRecentFile.setOnAction(_ -> {
                try {
                    openRecentFile(pathRecentFile);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            menuOpenRecentFile.getItems().add(menuItemRecentFile);
        }
    }


    private void editName(FileEntity fe){
        if(fe == null) return;
        runSubWindow(fe.getName(), "Edit name - "+fe.getFileType().toString());
        String newName = entryNameController.getName();
        if(newName.isEmpty() || newName.isBlank() || newName.equals(fe.getName()))
            return;

        fileFSTree.updateFilename(fe, newName);
        deselectListCell(Section.ALL);
    }

    private void removeFileEntity(FileEntity fe){
        if(fe == null) return;

    }

    private void cutFileEntity(FileEntity fe){
        if(fe == null) return;
        if(fileTransferStack.size() == 1) fileTransferStack.clear();
        fileTransferStack.push(fe);
        labelAction.setText("Cut");
        labelFileName.setText(fe.getName());
        labelPathFileName.setText(fe.getDirectoryPath().getPath());
        paneInfo.setVisible(true);
        JavaFXNotifications.notificationInformation(null, "File ready to move.");
        //falta distinguir
    }

    private void copyFileEntity(FileEntity fe){
        if(fe == null) return;
        if(fileTransferStack.size() == 1) fileTransferStack.clear();
        fileTransferStack.push(fe);
        labelAction.setText("Copy");
        labelFileName.setText(fe.getName());
        labelPathFileName.setText(fe.getDirectoryPath().getPath());
        paneInfo.setVisible(true);
        JavaFXNotifications.notificationInformation(null, "File ready to paste.");
    }

    private void copyToClipboard(FileEntity fe){
        if(fe == null) return;
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(fe.getPath().toString());
        clipboard.setContent(content);
        JavaFXNotifications.notificationInformation(null, "The path was copied to the clipboard.");
    }




    private void gotToDirectory(Path target){
        // Path.of("\\") -> root
        if(target == null) return;

        if(Objects.equals(target, pathIsSelected)) return;

        if(!target.equals(rootPath) && !fileAssignmentTable.contains(target)){
            textField_inputPath.clear();
            textField_inputPath.setText(generateValidPath(pathIsSelected.toString())); // check
            JavaFXGlobalExceptionHandler.alertError(
                    "Error",
                    "Error entering directory",
                    "Could not enter directory, check path."
            );
            return;
        }

        if(!target.getPath().equals(Path.separatorToUse)
                && fileAssignmentTable.get(target).getValue() instanceof PlainText){
            openTextEditor(fileAssignmentTable.get(target).getValue());
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
        tableView.getItems().addAll(fileFSTree.getFilesEntitiesInDirectory(pathIsSelected));
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
            JavaFXGlobalExceptionHandler.alertWarning(
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
        if(!fileFSTree.push(fe)) return false;
        TreeItem<FileEntity> currentFileTI = new TreeItem<>(fe);
        treeItemParent.getChildren().add(currentFileTI);
        fileAssignmentTable.put(fe.getPath(), currentFileTI);
        tableView.getItems().clear();
        tableView.getItems().addAll(fileFSTree.getFilesEntitiesInDirectory(fe.getDirectoryPath()));
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
        if(pathIsSelected.equals(rootPath)) return;
        Folder folder = new Folder(name, pathIsSelected);
        TreeItem<FileEntity> treeItemParent = fileAssignmentTable.get(folder.getDirectoryPath());
        createFile(folder, treeItemParent);
    }

    @FXML
    private void createPlainText(){
        runSubWindow("", "Create PlainText");
        String name = entryNameController.getName();
        if(name.isEmpty() || name.isBlank()) return;
        if(pathIsSelected.equals(rootPath)) return;
        PlainText plainText = new PlainText(name, pathIsSelected);
        TreeItem<FileEntity> treeItemParent = fileAssignmentTable.get(plainText.getDirectoryPath());
        createFile(plainText, treeItemParent);
    }

    @FXML
    private void createShortCut(){
        //if(pathIsSelected.equals(rootPath)) return;
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
        tableView.getItems().addAll(fileFSTree.getFilesEntitiesInDirectory(pathIsSelected));
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
        tableView.getItems().addAll(fileFSTree.getFilesEntitiesInDirectory(pathIsSelected));
    }

    @FXML
    private void convertToPhysicalDirectory(){
       ConfirmationOptions co = JavaFXGlobalExceptionHandler.alertConfirmation(
               "Create file directory on hard drive",
               "Do you wish to continue?", "This action can not be undone.");
       if(co != ConfirmationOptions.YES) return;

    }

    @FXML
    protected void test() throws Exception{
        //fileAssignmentTable.getKeys();
        //ObjectSerializationUtil.Serializable_(fileAssignmentTable, fileTree, "C:\\Users\\DERECK\\IdeaProjects\\FileBrowser\\src\\main\\resources\\pe\\edu\\utp\\filebrowser\\object1.ser");
        //ObjectSerializationUtil.test(new VirtualDiskDriver("asd", Path.of("asd")));



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
        JavaFXGlobalExceptionHandler.alertInformation(
                "Save",
                "Save content!",
                "The content of the file was saved!");
    }

    @FXML
    private void closeTextEditor(){
        if(!(selectedFilePT.getContent().equals(textAreaEditorText.getText()))) {
            ConfirmationOptions co = JavaFXGlobalExceptionHandler.alertConfirmation(
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
        return Path.generateValidPath(path, true);
    }

    private Path generateValidPath(Path path){
        String auxPath = generateValidPath(path.toString());
        return new Path(auxPath);
    }

    @FXML
    private void navigateToDirectory(){
        String inputPath = textField_inputPath.getText();
        Path path = new Path(generateValidPath(inputPath));
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