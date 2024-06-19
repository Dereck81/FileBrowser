package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.layout.Pane;

import java.io.Serializable;
import pe.edu.utp.filebrowser.FileSystem.Path;
import java.time.LocalDateTime;

public class VirtualDiskDriver extends FileEntity implements Serializable {

    public VirtualDiskDriver(String virtualDiskName, Path directoryPath){
        super(virtualDiskName, FileTypes.VIRTUALDISK, directoryPath, LocalDateTime.now());
    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public LocalDateTime getModificationDate() {
        return super.getModificationDate();
    }

    /**
     * It is based on the Windows file manager,
     * which only modifies the date when a file is added
     * and not when its name or path is modified.
     */
    public void setModificationDate() {
        super.setModificationDate(LocalDateTime.now());
    }

    public FileTypes getFileType(){
        return super.getFileType();
    }


    public Pane getPane(){
        return super.getPane();
    }

}
