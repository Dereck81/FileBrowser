package pe.edu.utp.filebrowser.FileSystem;


import java.io.Serializable;
import pe.edu.utp.filebrowser.Enums.FileTypes;
import java.time.LocalDateTime;

public class Folder extends FileEntity implements Serializable {

    public Folder(String folderName, FileEntity fileEntityParent) {
        super(folderName, FileTypes.FOLDER, fileEntityParent, LocalDateTime.now());
    }

    public Folder(String folderName, FileEntity fileEntityParent,
                  LocalDateTime modificationDate, LocalDateTime creationDate) {
        super(folderName, FileTypes.FOLDER, fileEntityParent, modificationDate, creationDate);
    }

    public void setModificationDate(){
        super.setModificationDate(LocalDateTime.now());
    }

}
