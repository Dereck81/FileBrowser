package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.layout.Pane;

import java.io.Serializable;

import pe.edu.utp.filebrowser.Enums.FileTypes;

import java.time.LocalDateTime;

public class Folder extends FileEntity implements Serializable {
    //private DynamicArray<FileEntity> fileContainer;

    //public Folder(String folderName, Path directoryPath) {
    //    super(folderName, FileTypes.FOLDER, directoryPath, LocalDateTime.now());
    //}

    public Folder(String folderName, FileEntity fileEntityParent) {
        super(folderName, FileTypes.FOLDER, fileEntityParent, LocalDateTime.now());
    }

    public void setModificationDate(){
        super.setModificationDate(LocalDateTime.now());
    }

}
