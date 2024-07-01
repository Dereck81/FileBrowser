package pe.edu.utp.filebrowser.FileSystem;

import java.io.Serializable;

import pe.edu.utp.filebrowser.Enums.FileTypes;

import java.time.LocalDateTime;

public class VirtualDiskDriver extends FileEntity implements Serializable {

    public VirtualDiskDriver(String virtualDiskName, Path directoryPath){
        super(virtualDiskName, FileTypes.VIRTUALDISK, directoryPath, LocalDateTime.now());
    }

    public VirtualDiskDriver(String virtualDiskName, Path directoryPath,
                             LocalDateTime modificationDate, LocalDateTime creationDate){
        super(virtualDiskName, FileTypes.VIRTUALDISK, directoryPath, modificationDate, creationDate);
    }

    public LocalDateTime getModificationDate() {
        return super.getModificationDate();
    }

    public void setModificationDate() {
        super.setModificationDate(LocalDateTime.now());
    }

}
