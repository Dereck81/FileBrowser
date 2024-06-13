package pe.edu.utp.filebrowser.FileSystem;

import pe.edu.utp.filebrowser.DSA.DynamicArray;

import java.time.LocalDate;

public class VirtualDiskDriver extends FileEntity{
    private String virtualDiskName;
    private DynamicArray<FileEntity> fileContainer;

    public VirtualDiskDriver(String virtualDiskName){
        super(FileTypes.VIRTUALDISK, null, LocalDate.now());
        this.virtualDiskName = virtualDiskName;
    }

    public void addFile(FileEntity file){
        fileContainer.pushBack(file);
        setModificationDate();
    }

    public void addFiles(FileEntity...files){
        for(FileEntity file : files) fileContainer.pushBack(file);
        setModificationDate();
    }


    public String getVirtualDiskName() {
        return virtualDiskName;
    }

    public void setVirtualDiskName(String virtualDiskName) {
        this.virtualDiskName = virtualDiskName;
    }

    public LocalDate getModificationDate() {
        return super.getModificationDate();
    }

    /**
     * It is based on the Windows file manager,
     * which only modifies the date when a file is added
     * and not when its name or path is modified.
     */
    private void setModificationDate() {
        super.setModificationDate(LocalDate.now());
    }

    public FileTypes getFileType(){
        return super.getFileTypes();
    }



}
