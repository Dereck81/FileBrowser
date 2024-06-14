package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.layout.Pane;
import pe.edu.utp.filebrowser.DSA.DynamicArray;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class Folder extends FileEntity{
    //private DynamicArray<FileEntity> fileContainer;

    public Folder(String folderName, Path directoryPath) {
        super(folderName, FileTypes.FOLDER, directoryPath, LocalDateTime.now());
    }

    /*
    public void addFile(FileEntity file){
        fileContainer.pushBack(file);
        setModificationDate();
    }

    public void addFiles(FileEntity...files){
        for (FileEntity file : files) fileContainer.pushBack(file);
        setModificationDate();
    }

     */

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

    private void setModificationDate(){
        super.setModificationDate(LocalDateTime.now());
    }

    public FileTypes getFileType() {
        return super.getFileType();
    }

    public Pane getPane() {
        return super.getPane();
    }
}
