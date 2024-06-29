package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.layout.Pane;

import java.io.Serializable;

import pe.edu.utp.filebrowser.Enums.FileTypes;

import java.time.LocalDateTime;

public class Folder extends FileEntity implements Serializable {
    //private DynamicArray<FileEntity> fileContainer;

    public Folder(String folderName, Path directoryPath) {
        super(folderName, FileTypes.FOLDER, directoryPath, LocalDateTime.now());
    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public Path getPath(){
        return super.getPath();
    }

    public void setDirectoryPath(Path directoryPath){
        super.setDirectoryPath(directoryPath);
    }

    public Path getDirectoryPath(){
        return super.getDirectoryPath();
    }

    /*
    public DynamicArray<FileEntity> getFileContainer() {
        return fileContainer;
    }

     */

    public LocalDateTime getModificationDate() {
        return super.getModificationDate();
    }

    public void setModificationDate(){
        super.setModificationDate(LocalDateTime.now());
    }

    public FileTypes getFileType() {
        return super.getFileType();
    }

    public Pane getPane() {
        return super.getPane();
    }
}
