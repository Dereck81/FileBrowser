package pe.edu.utp.filebrowser.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.utp.filebrowser.DSA.*;
import pe.edu.utp.filebrowser.FileBrowser;
import pe.edu.utp.filebrowser.FileSystem.*;
import pe.edu.utp.filebrowser.IO.IO;
import pe.edu.utp.filebrowser.IO.SystemElementCreator;
import pe.edu.utp.filebrowser.OS.OSUtils;
import pe.edu.utp.filebrowser.TreeAndTable.CellFactory;
import pe.edu.utp.filebrowser.FileSystem.RootItem;
import pe.edu.utp.filebrowser.Enums.ConfirmationOptions;
import pe.edu.utp.filebrowser.Utilites.JavaFXGlobalExceptionHandler;
import pe.edu.utp.filebrowser.Enums.Section;
import pe.edu.utp.filebrowser.Enums.FileTypes;

import static pe.edu.utp.filebrowser.TreeAndTable.CellUtilityManager.handleSelectedCellPressed;

import java.io.*;

import pe.edu.utp.filebrowser.FileSystem.Path;
import pe.edu.utp.filebrowser.Utilites.JavaFXNotifications;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class FileBrowserController {

    // FXML tags
    // TreeItem
    private TreeItem<FileEntity> rootItemMP = new TreeItem<>(new RootItem("My PC", FileTypes.PC));
    private final TreeItem<FileEntity> rootItemDA = new TreeItem<>(new RootItem("My Direct Access", FileTypes.DIRECTACCESS));

    // MenuBar
    @FXML
    private MenuBar menuBar;

    // Menu
    @FXML
    private Menu menuOpenRecentFile, menuActions;

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
    private Button buttonBackwardPath, buttonForwardPath, buttonClearSearch, buttonUpDirectory;

    // JavaFX element's without @FXML tag
    // JavaFX ContextMenus
    private final ContextMenu contextMenuTableV = new ContextMenu();
    private final ContextMenu contextMenuTreeMP = new ContextMenu();
    private final ContextMenu contextMenuTreeDA = new ContextMenu();
    private final ContextMenu contextMenuFO = new ContextMenu();

    // File Chooser
    private FileChooser fileChooserSaveFile, fileChooserOpen;

    // Directory Chooser
    private DirectoryChooser directoryChooser;

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
    private FSTree fileFSTree = new FSTree();

    // HashMap
    private final HashMap<Path, TreeItem<FileEntity>> fileAssignmentTable = new HashMap<>();
    private final HashMap<Path, TreeItem<FileEntity>> fileAssignmentTableDA = new HashMap<>();

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

    // region [SETUP METHODS]

    private void setupFileChoosers() {
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
        // directoryChooser
        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select directory to create the file system");
        directoryChooser.setInitialDirectory(new File(OSUtils.getProperty("user.home")));
    }

    private void setupContextMenu() {
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
        MenuItem paste = new MenuItem("Paste");
        paste.setOnAction(_ -> pasteFileEntity());

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

            if (fe == null) return;

            createShortCut();
        });

        MenuItem createFileDirectoryHardDiskV = new MenuItem("Create file directory on hard disk");
        createFileDirectoryHardDiskV.setOnAction( _ -> {
            FileEntity fe = tableView.getSelectionModel().getSelectedItem();

            if (fe == null) return;

            try {
                convertToPhysicalDirectory(fileFSTree.getNode(fe.getPath()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        contextMenuTableV.getItems().setAll(createShortCutTableV, createFileDirectoryHardDiskV, editNameTableV, copyPathTableV, copy, cut, delete);
        contextMenuFO.getItems().setAll(createDiskMI, createFolderMI, createPlainTextMI, paste);
        contextMenuTreeDA.getItems().setAll(editNameTreeDA, copyPathTreeDA);
        contextMenuTreeMP.getItems().setAll(editNameTreeMP, copyPathTreeMP);
    }

    private void setupTableView() {
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

            if (row instanceof PlainText) result = row.getSize() + " Bytes";
            else if (row instanceof DirectAccess)
                if (((DirectAccess) row).getTargetFile() instanceof PlainText)
                    result = row.getSize() + " Bytes";

            return new javafx.beans.property
                    .SimpleStringProperty(result);
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((_) -> {
            if (isDeselectionByUser) deselectListCell(Section.TREEVIEW);
        });

        tableView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                FileEntity fe = tableView.getSelectionModel().getSelectedItem();
                if (fe == null) return;

                if (fe instanceof DirectAccess)
                    fe = ((DirectAccess) fe).getTargetFile();

                if (fe instanceof PlainText) {
                    openTextEditor(fe);
                    return;
                }

                goToDirectory(fe.getPath());
            } else if (keyEvent.getCode() == KeyCode.DELETE
                    || keyEvent.getCode() == KeyCode.BACK_SPACE) {

                if(!buttonClearSearch.isDisable()) return;

                FileEntity fe = tableView.getSelectionModel().getSelectedItem();

                if (fe == null) return;

                removeFileEntity(fe);
            }
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

                if (!handleSelectedCellPressed(tableView, event)) {
                    deselectListCell(Section.TABLEVIEW);
                    return;
                }

                FileEntity item = tableView.getSelectionModel().getSelectedItem();

                if (item instanceof DirectAccess)
                    item = ((DirectAccess) item).getTargetFile();

                if (item instanceof PlainText) {
                    openTextEditor(item);
                    return;
                }

                goToDirectory(item.getPath());
            } else if (event.getButton() == MouseButton.SECONDARY) {
                if(!buttonClearSearch.isDisable()) return; //

                if (!handleSelectedCellPressed(tableView, event)) {
                    tableView.setContextMenu(contextMenuFO);
                    deselectListCell(Section.TABLEVIEW);
                } else tableView.setContextMenu(contextMenuTableV);
            }
        });
    }

    private void setupTreeView() {
        treeViewMP.setCellFactory((_) -> CellFactory.setCellFactoryTree());
        treeViewMP.setRoot(rootItemMP);
        treeViewDA.setCellFactory((_) -> CellFactory.setCellFactoryTree());
        treeViewDA.setRoot(rootItemDA);

        treeViewDA.getSelectionModel().selectedItemProperty().addListener((_) -> {
            if (isDeselectionByUser) deselectListCell(Section.TREEVIEW_MYFILES, Section.TABLEVIEW);
        });

        treeViewMP.getSelectionModel().selectedItemProperty().addListener((_) -> {
            if (isDeselectionByUser) deselectListCell(Section.TREEVIEW_DIRECTACCESS, Section.TABLEVIEW);
        });

        treeViewMP.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if(!buttonClearSearch.isDisable()) return;
                if (!handleSelectedCellPressed(treeViewMP, event)) {
                    deselectListCell(Section.TREEVIEW_MYFILES);
                    return;
                }

                TreeItem<FileEntity> item = treeViewMP.getSelectionModel().getSelectedItem();
                FileEntity itemValue = item.getValue();

                if (Objects.equals(item, rootItemMP)) {
                    goToDirectory(rootItemMP.getValue().getDirectoryPath());
                    return;
                }

                if (itemValue instanceof DirectAccess)
                    itemValue = ((DirectAccess) itemValue).getTargetFile();

                if (itemValue instanceof PlainText) {
                    openTextEditor(itemValue);
                    return;
                }

                goToDirectory(itemValue.getPath());
            } else {
                if(!buttonClearSearch.isDisable()) return;
                showContextMenu(event, treeViewMP, rootItemMP, contextMenuTreeMP);
            }
        });

        treeViewDA.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if(!buttonClearSearch.isDisable()) return;

                if (!handleSelectedCellPressed(treeViewDA, event)) {
                    deselectListCell(Section.TREEVIEW_DIRECTACCESS);
                    return;
                }

                TreeItem<FileEntity> item = treeViewDA.getSelectionModel().getSelectedItem();
                FileEntity itemValue = item.getValue();

                if (Objects.equals(rootItemDA, item)) return;

                if (itemValue instanceof DirectAccess)
                    itemValue = ((DirectAccess) itemValue).getTargetFile();

                if (itemValue instanceof PlainText) {
                    openTextEditor(itemValue);
                    return;
                }

                goToDirectory(itemValue.getPath());
            } else {
                if(!buttonClearSearch.isDisable()) return;

                showContextMenu(event, treeViewDA, rootItemDA, contextMenuTreeDA);
            }
        });

    }

    private void createSubWindows(){
        FXMLLoader loaderEntryName = new FXMLLoader(FileBrowser.class.getResource("Input.fxml"));
        try{
            // creates the EntryName subwindow
            Scene sceneEntryName = new Scene(loaderEntryName.load(), 349, 71);
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

    private void showContextMenu(MouseEvent event, TreeView<FileEntity> treeView, TreeItem<FileEntity> rootItem, ContextMenu contextMenu) {
        if (event.getButton() == MouseButton.SECONDARY) {
            TreeItem<FileEntity> ti = treeView.getSelectionModel().getSelectedItem();

            if (ti == null || ti == rootItem) {
                treeView.setContextMenu(null);
                return;
            }
            if (!handleSelectedCellPressed(treeView, event)) {
                treeView.setContextMenu(null);
            } else treeView.setContextMenu(contextMenu);
        }
    }

    public void setStage(Stage stage){
        this.primaryStage = stage;
    }

    // endregion

    // region [METHODS THAT HANDLE FILES]

    @FXML
    private void openFile() {
        file = fileChooserOpen.showOpenDialog(primaryStage);

        if (file == null) return;

        IO.writeRecordRecentFiles(file.getPath());
        deserializeEverything(file);
        updateRecentFiles();
    }

    @FXML
    private void saveFile(){
        ConfirmationOptions co = JavaFXGlobalExceptionHandler.alertConfirmation(
                "Save file",
                "Save file",
                "Do you want to save the file?"
        );
        if (co != ConfirmationOptions.YES) return;

        file = fileChooserSaveFile.showSaveDialog(primaryStage);

        if (file == null) return;

        serializeEverything(file);

        JavaFXGlobalExceptionHandler.alertInformation(
                "Saved file",
                "Saved file",
                "The changes were saved to the file indicated."
        );
    }

    @FXML
    private void closeFile() {
        resetApplication();
    }

    /**
     * Opens a recently accessed file based on the selected menu item.
     *
     * @param pathRecentFile The path of the recently accessed file.
     */
    private void openRecentFile(String pathRecentFile) throws FileNotFoundException {
        file = new File(pathRecentFile);

        if (!file.exists()) {
            IO.deleteRecentFilesRecord(pathRecentFile);
            updateRecentFiles();
            throw new FileNotFoundException("File not found: " + pathRecentFile);
        }
    }

    /**
     * Updates the list of recent files in the menu.
     */
    private void updateRecentFiles() {
        String[] directorySeparators = new String[]{"/", "\\"};
        ArrayList<String> recentFiles = IO.readRecordRecentFiles();
        int index = 0;

        if (menuOpenRecentFile != null) menuOpenRecentFile.getItems().clear();

        for (String pathRecentFile : recentFiles.toArray(new String[0])) {
            for (String ds : directorySeparators) {
                index = pathRecentFile.lastIndexOf(ds);
                if (index != -1)
                    break;
            }
            String nameFile = pathRecentFile.substring(index + 1);
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

    // endregion

    // region [FUNCTIONALITIES]

    private void editName(FileEntity fe) {
        if (fe == null) return;

        runSubWindow(fe.getName(), "Edit name - " + fe.getFileType().toString(), fe.getFileType());
        String newName = entryNameController.getName();

        if (newName.isEmpty() || newName.isBlank() || newName.equals(fe.getName()))
            return;

        if (fileAssignmentTable.contains(fe.getDirectoryPath().resolve(newName))) {
            JavaFXGlobalExceptionHandler.alertError(
                    "Error",
                    "Error editing name",
                    "Could not edit the name because a file with the same name already exists"
            );
            return;
        }
        Path oldPath = fe.getPath();

        if (fe instanceof DirectAccess) {
            fe.setName(newName);
            updateFAT(oldPath, fileAssignmentTable);
            updateFAT(oldPath, fileAssignmentTableDA);
            return;
        }

        fileFSTree.updateFilename(fe, newName);

        updateFAT(oldPath, fileAssignmentTable);

        tableView.refresh();
        treeViewMP.refresh();
        treeViewDA.refresh();

        deselectListCell(Section.ALL);
    }

    private void cutFileEntity(FileEntity fe) {
        if (fe == null || fe instanceof VirtualDiskDriver) return;

        if (fileTransferStack.size() == 1) fileTransferStack.clear();

        fileTransferStack.push(fe);
        labelAction.setText("Cut");
        labelFileName.setText(fe.getName());
        labelPathFileName.setText(fe.getDirectoryPath().getPath());
        paneInfo.setVisible(true);

        JavaFXNotifications.notificationInformation(null, "File ready to move.");
    }

    private void copyFileEntity(FileEntity fe) {
        if (fe == null || fe instanceof VirtualDiskDriver) return;

        if (fileTransferStack.size() == 1) fileTransferStack.clear();

        fileTransferStack.push(fe);
        labelAction.setText("Copy");
        labelFileName.setText(fe.getName());
        labelPathFileName.setText(fe.getDirectoryPath().getPath());
        paneInfo.setVisible(true);

        JavaFXNotifications.notificationInformation(null, "File ready to paste.");
    }

    private void pasteFileEntity(){
        if(pathIsSelected == rootPath) {
            JavaFXGlobalExceptionHandler.alertError(
                    "Error",
                    "Error when pasting file",
                    "Cannot paste file in this path."
            );
            return;
        }
        if(labelAction.getText().equals("Cut"))
            pasteFileEntityCut();
        else if (labelAction.getText().equals("Copy"))
            pasteFileEntityCopy();
    }

    private void pasteFileEntityCut(){
        FileEntity fe = fileTransferStack.pop();

        if(fe == null) return;

        if(fe.getDirectoryPath() == pathIsSelected) {
            resetInformationPane();
            return;
        }

        Path oldPath = fe.getPath();

        TreeItem<FileEntity> parent = fileAssignmentTable.get(fe.getDirectoryPath());
        TreeItem<FileEntity> child = fileAssignmentTable.get(fe.getPath());

        if(!fileFSTree.move(fe, pathIsSelected)) return;

        TreeItem<FileEntity> newParent = fileAssignmentTable.get(fe.getDirectoryPath());

        parent.getChildren().remove(child);
        newParent.getChildren().add(child);

        updateFAT(oldPath, fileAssignmentTable);
        updateFAT(oldPath, fileAssignmentTableDA);

        treeViewDA.refresh();
        treeViewMP.refresh();
        tableView.getItems().clear();
        tableView.getItems().addAll(fileFSTree.getFilesEntitiesInDirectory(pathIsSelected));

        resetInformationPane();
    }

    private void pasteFileEntityCopy(){
        FileEntity fe = fileTransferStack.pop();

        if(fe == null) return;

        if(fe.getDirectoryPath() == pathIsSelected) {
            resetInformationPane();
            return;
        }

        if(!fileFSTree.copy(fe, pathIsSelected)) return;

        TreeItem<FileEntity> newTI = rebuildFSTree(fileFSTree.getNode(pathIsSelected));
        fileAssignmentTable.get(pathIsSelected).getChildren().addAll(newTI.getChildren());

        treeViewDA.refresh();
        treeViewMP.refresh();
        tableView.getItems().clear();
        tableView.getItems().addAll(fileFSTree.getFilesEntitiesInDirectory(pathIsSelected));

        resetInformationPane();
    }

    private void resetInformationPane(){
        labelAction.setText("None");
        labelFileName.setText("None");
        labelPathFileName.setText("None");
    }

    private void copyToClipboard(FileEntity fe) {
        if (fe == null) return;

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        content.putString(fe.getPath().toString());
        clipboard.setContent(content);

        JavaFXNotifications.notificationInformation(null, "The path was copied to the clipboard.");
    }

    private void removeFileEntity(FileEntity fe) {
        if (fe == null) return;

        ConfirmationOptions co = JavaFXGlobalExceptionHandler.alertConfirmation(
                "Eliminate",
                "Delete the file",
                "Do you want to delete this file? This operation cannot be undone"
        );

        if (co != ConfirmationOptions.YES) return;

        TreeItem<FileEntity> parent = fileAssignmentTable.get(fe.getDirectoryPath());
        TreeItem<FileEntity> child = fileAssignmentTable.get(fe.getPath());

        fileFSTree.remove(fe);
        removeFAT(fe.getPath(), fileAssignmentTable);

        if (fe instanceof VirtualDiskDriver) rootItemMP.getChildren().remove(child);
        else parent.getChildren().remove(child);

        Object[] keys = fileAssignmentTableDA.getKeys(
                createPathMatchingPredicate(fe.getPath().toString()));

        for (int i = 0; i < keys.length; i++)
            rootItemDA.getChildren().remove(fileAssignmentTableDA.get((Path) keys[i]));

        if(keys.length > 0)
            removeFAT(fe.getPath(), fileAssignmentTableDA);

        treeViewDA.refresh();
        treeViewMP.refresh();
        tableView.getItems().clear();
        tableView.getItems().addAll(fileFSTree.getFilesEntitiesInDirectory(pathIsSelected));
    }

    @FXML
    private void convertToPhysicalDirectory() throws Exception {
        ConfirmationOptions co = JavaFXGlobalExceptionHandler.alertConfirmation(
                "Create file directory on hard drive",
                "Do you wish to continue?", "This action can not be undone.");

        if(co != ConfirmationOptions.YES) return;

        File directory =  directoryChooser.showDialog(primaryStage);

        if (directory == null) return;

        FileEntity fe = tableView.getSelectionModel().getSelectedItem();

        if (fe == null)
            SystemElementCreator.createFileSystemStructure(
                    fileFSTree.getRoot(), directory.getPath()
            );
        else
            SystemElementCreator.createFileSystemStructure(
                    fileFSTree.getNode(fe.getPath()), directory.getPath()
            );

        JavaFXGlobalExceptionHandler.alertInformation(
                "Completed process",
                "File export completed",
                "All files created in the program were exported."
        );
    }

    private void convertToPhysicalDirectory(FileNode fn) throws Exception {
        ConfirmationOptions co = JavaFXGlobalExceptionHandler.alertConfirmation(
                "Create file directory on hard drive",
                "Do you wish to continue?", "This action can not be undone.");

        if(co != ConfirmationOptions.YES) return;

        File directory =  directoryChooser.showDialog(primaryStage);

        if (directory == null) return;

        SystemElementCreator.createFileSystemStructure(
                fn, directory.getPath()
        );

        JavaFXGlobalExceptionHandler.alertInformation(
                "Completed process",
                "File export completed",
                "All files created in the program were exported."
        );
    }

    @FXML
    public void quit(){
        Platform.exit();
    }

    // endregion

    // region [METHODS TO MANAGE FAT]

    private void updateFAT(Path oldPath, HashMap<Path, TreeItem<FileEntity>> fat){
        String escapedPathOld = Pattern.quote(oldPath.getPath());
        String regex = "^" + escapedPathOld + "("+Path.separatorToUseRgx+".*)?$";

        Predicate<Path> condition = key -> key.getPath().matches(regex);
        KeyUpdater<Path, TreeItem<FileEntity>> updater = (_, value) -> value.getValue().getPath();

        fat.update(condition, updater);
    }

    private void removeFAT(Path oldPath, HashMap<Path, TreeItem<FileEntity>> fat){
        String escapedPathOld = Pattern.quote(oldPath.toString());
        String regex = "^" + escapedPathOld + "("+Path.separatorToUseRgx+".*)?$";

        Predicate<Path> condition = key -> key.getPath().matches(regex);

        fat.remove(condition);
    }

    private Predicate<Path> createPathMatchingPredicate(String oldPath){
        String escapedOldPath = Pattern.quote(oldPath);
        String regex = "^" + escapedOldPath + "("+Path.separatorToUseRgx+".*)?$";

        return key -> key.getPath().matches(regex);
    }

    // endregion

    // region [METHODS FOR SUBWINDOWS]

    private void runSubWindow(String entryName, String title, FileTypes fileType){
        entryNameController.setName(entryName);
        entryNameController.setTitle(title);
        entryNameController.setFileType(fileType);
        stageEntryName.showAndWait();
    }

    // endregion

    // region [FILE CREATION METHODS]

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
        runSubWindow("", "Create Virtual Disk", FileTypes.VIRTUALDISK);
        String name = entryNameController.getName();

        if(name.isEmpty() || name.isBlank()) return;

        VirtualDiskDriver vdd = new VirtualDiskDriver(name, rootItemMP.getValue().getDirectoryPath());

        createFile(vdd, rootItemMP);
    }

    @FXML
    private void createFolder(){
        runSubWindow("", "Create Folder", FileTypes.FOLDER);
        String name = entryNameController.getName();

        if(name.isEmpty() || name.isBlank()) return;

        if(pathIsSelected.equals(rootPath)) return;

        FileEntity feParent = fileFSTree.getFileEntityByPath(pathIsSelected);
        Folder folder = new Folder(name, feParent);

        TreeItem<FileEntity> treeItemParent = fileAssignmentTable.get(folder.getDirectoryPath());

        createFile(folder, treeItemParent);
    }

    @FXML
    private void createPlainText(){
        runSubWindow("", "Create PlainText", FileTypes.PLAINTEXT);
        String name = entryNameController.getName();

        if(name.isEmpty() || name.isBlank()) return;

        if(pathIsSelected.equals(rootPath)) return;

        FileEntity feParent = fileFSTree.getFileEntityByPath(pathIsSelected);
        PlainText plainText = new PlainText(name, feParent);

        TreeItem<FileEntity> treeItemParent = fileAssignmentTable.get(plainText.getDirectoryPath());

        createFile(plainText, treeItemParent);
    }

    @FXML
    private void createShortCut(){
        runSubWindow("", "Create ShortCut", FileTypes.UNKOWN);

        FileEntity targetFile = tableView.getSelectionModel().getSelectedItem();
        String name = entryNameController.getName();

        if(targetFile == null) return;

        if(name.isEmpty() || name.isBlank()) name = targetFile.getName();

        name += " - Direct Access";

        DirectAccess directAccess;

        if(pathIsSelected.equals(rootPath))
            directAccess = new DirectAccess(name, targetFile.getPath(), targetFile);
        else {
            FileEntity feParent = fileFSTree.getFileEntityByPath(pathIsSelected);
            directAccess = new DirectAccess(name, feParent, targetFile);
        }

        // modify when moving said shortcut (disk shortcut)
        TreeItem<FileEntity> treeItemParent = fileAssignmentTable.get(directAccess.getShortcutDirectoryPath());
        TreeItem<FileEntity> currentFileTI1 = new TreeItem<>(directAccess);

        if(createFile(directAccess, treeItemParent)) {
            fileAssignmentTableDA.put(directAccess.getPath(), currentFileTI1);
            rootItemDA.getChildren().add(currentFileTI1);
        }
    }

    // endregion

    // region [METHODS FOR DIRECTORY NAVIGATION]

    private void goToDirectory(Path target) {

        if (target == null) return;

        if (Objects.equals(target, pathIsSelected)){
            tableView.getItems().clear();
            tableView.getItems().addAll(fileFSTree.getFilesEntitiesInDirectory(pathIsSelected));
            return;
        }

        if (!target.equals(rootPath) && !fileAssignmentTable.contains(target)) {
            textField_inputPath.clear();
            textField_inputPath.setText(generateValidPath(pathIsSelected.toString())); // check

            JavaFXGlobalExceptionHandler.alertError(
                    "Error",
                    "Error entering directory",
                    "Could not enter directory, check path."
            );

            return;
        }

        if (!target.getPath().equals(Path.separatorToUse)
                && fileAssignmentTable.get(target).getValue() instanceof PlainText) {
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

        buttonUpDirectory.setDisable(pathIsSelected.equals(rootPath));

    }

    @FXML
    private void navigateToDirectory(){
        String inputPath = textField_inputPath.getText();
        Path path = new Path(generateValidPath(inputPath));

        if(Objects.equals(path, BackwardPathStack.peek())) backwardPath();

        else goToDirectory(path);
    }

    @FXML
    private void navigateUpDirectory(){
        buttonUpDirectory.setDisable(pathIsSelected.equals(rootPath));

        if(pathIsSelected.equals(rootPath)) return;

        goToDirectory(pathIsSelected.getParent());
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

        buttonUpDirectory.setDisable(pathIsSelected.equals(rootPath));
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

        buttonUpDirectory.setDisable(pathIsSelected.equals(rootPath));
    }

    private String generateValidPath(String path) {
        return Path.generateValidPath(path, true);
    }

    // endregion

    // region [TEXT EDITOR METHODS]

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

    // endregion

    // region [METHODS FOR THE SEARCH FUNCTION]
    @FXML
    private void search(){
        String target = textField_search.getText();

        if(target.isEmpty() || target.isBlank()) return;

        // Line for exact matches
        //String regex = "^.*("+Path.separatorToUseRgx+")"+target+"$";
        //Predicate<Path> condition = key -> key.getPath().matches(regex);

        // Line for partial matches
        Predicate<Path> condition = key -> {
            int idx = key.getPath().lastIndexOf(Path.separatorToUse);

            return key.getPath().substring(idx+1)
                    .toLowerCase().contains(target.toLowerCase());
        };

        Object[] keys = fileAssignmentTable.getKeys(condition);
        tableView.getItems().clear();

        for (int i = 0; i < keys.length; i++)
            tableView.getItems().add(fileAssignmentTable.get((Path) keys[i]).getValue());

        buttonBackwardPath.setDisable(true);
        buttonForwardPath.setDisable(true);
        buttonClearSearch.setDisable(false);
        textField_inputPath.setDisable(true);
        buttonUpDirectory.setDisable(true);
        menuActions.setDisable(true);
    }

    @FXML
    private void clearSearch(){
        tableView.getItems().clear();
        tableView.getItems().addAll(fileFSTree.getFilesEntitiesInDirectory(pathIsSelected));
        textField_search.clear();

        buttonForwardPath.setDisable(false);
        buttonBackwardPath.setDisable(false);
        buttonClearSearch.setDisable(true);
        textField_inputPath.setDisable(false);
        buttonUpDirectory.setDisable(pathIsSelected == rootPath);
        menuActions.setDisable(false);
    }

    // endregion

    // region [SERIALIZATION]

    private void serializeEverything(File out) {
        try {
            FileOutputStream fileOut = new FileOutputStream(out);
            ObjectOutputStream objectWriter = new ObjectOutputStream(fileOut);

            objectWriter.writeObject(fileFSTree);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TreeItem<FileEntity> rebuildFSTree(FileNode root) {
        if (root.getFile() instanceof DirectAccess) {
            TreeItem<FileEntity> thisTreeItemDA = new TreeItem<>(root.getFile());

            fileAssignmentTableDA.put(root.getFile().getPath(), thisTreeItemDA);
            rootItemDA.getChildren().add(thisTreeItemDA);
        }

        TreeItem<FileEntity> thisTreeItem = new TreeItem<>(root.getFile());

        fileAssignmentTable.put(thisTreeItem.getValue().getPath(), thisTreeItem);

        for (FileNode child : root.getChildren())
            thisTreeItem.getChildren().add(rebuildFSTree(child));

        return thisTreeItem;
    }

    private void deserializeEverything(File in) {
        try {
            FileInputStream fileIn = new FileInputStream(in);
            ObjectInputStream objectReader = new ObjectInputStream(fileIn);

            resetApplication();

            fileFSTree = (FSTree) objectReader.readObject();

            // rebuild TreeView and fileAssignmentTable
            rootItemMP = rebuildFSTree(fileFSTree.getRoot());
            treeViewMP.setRoot(rootItemMP);
            treeViewMP.refresh();
            treeViewDA.refresh();

            goToDirectory(rootItemMP.getValue().getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid file format");

            JavaFXGlobalExceptionHandler.alertError(
                    "Error",
                    "Error",
                    "Invalid file format"
            );

            throw new RuntimeException(e);
        }
    }

    // endregion

    // region [OTHERS METHODS]

    @FXML
    protected void test(){
    }

    private void resetApplication(){
        ForwardPathStack.clear();
        BackwardPathStack.clear();
        fileTransferStack.clear();

        textField_search.clear();
        textField_inputPath.clear();

        fileAssignmentTable.clear();
        fileAssignmentTableDA.clear();
        fileFSTree.clear();

        rootItemMP.getChildren().clear();
        rootItemDA.getChildren().clear();

        tableView.getItems().clear();

        buttonForwardPath.setDisable(true);
        buttonBackwardPath.setDisable(true);
        buttonClearSearch.setDisable(true);
        buttonUpDirectory.setDisable(true);

        menuActions.setDisable(false);

        pathIsSelected = rootPath;

        resetInformationPane();
    }

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

    // endregion

}