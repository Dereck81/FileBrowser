package pe.edu.utp.filebrowser.FileSystem;

import pe.edu.utp.filebrowser.DSA.DynamicArray;
import javafx.scene.layout.Pane;

import java.time.LocalDateTime;

public class VirtualDiskDriver extends FileEntity{
    //private DynamicArray<FileEntity> fileContainer;

    public VirtualDiskDriver(String virtualDiskName){
        super(virtualDiskName, FileTypes.VIRTUALDISK, null, LocalDateTime.now());
    }

    /*
    public void addFile(FileEntity file){
        fileContainer.pushBack(file);
        setModificationDate();
    }

    public void addFiles(FileEntity...files){
        for(FileEntity file : files) fileContainer.pushBack(file);
        setModificationDate();
    }
     */


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
    private void setModificationDate() {
        super.setModificationDate(LocalDateTime.now());
    }

    public FileTypes getFileType(){
        return super.getFileType();
    }


    public Pane getPane(){
        return super.getPane();
    }

}
