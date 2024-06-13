package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.layout.Pane;
import pe.edu.utp.filebrowser.DSA.DynamicArray;

import java.time.LocalDateTime;

public class Folder extends FileEntity{
    private String name;
    private DynamicArray<FileEntity> fileContainer;

    public Folder(String folderName, String directoryPath) {
        super(FileTypes.FOLDER, directoryPath, LocalDateTime.now());
        this.name = folderName;
    }

    public void addFile(FileEntity file){
        fileContainer.pushBack(file);
        setModificationDate();
    }

    public void addFiles(FileEntity...files){
        for (FileEntity file : files) fileContainer.pushBack(file);
        setModificationDate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectoryPath(){
        return super.getPath();
    }

    public void setDirectoryPath(String directoryPath){
        super.setPath(directoryPath);
    }

    public DynamicArray<FileEntity> getFileContainer() {
        return fileContainer;
    }

    public LocalDateTime getModificationDate() {
        return super.getModificationDate();
    }

    private void setModificationDate(){
        super.setModificationDate(LocalDateTime.now());
    }

    public FileTypes getFileType() {
        return super.getFileType();
    }

    public Pane getPane() {
        return super.getPane(name);
    }
}
