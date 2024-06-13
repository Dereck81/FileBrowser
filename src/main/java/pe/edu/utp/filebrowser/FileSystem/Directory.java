package pe.edu.utp.filebrowser.FileSystem;

import pe.edu.utp.filebrowser.DSA.DynamicArray;

import java.time.LocalDate;

public class Directory extends FileEntity{
    private String directoryName;
    private DynamicArray<FileEntity> fileContainer;

    public Directory(String directoryName, String directoryPath) {
        super(FileTypes.DIRECTORY, directoryPath, LocalDate.now());
        this.directoryName = directoryName;
    }

    public void addFile(FileEntity file){
        fileContainer.pushBack(file);
        setModificationDate();
    }

    public void addFiles(FileEntity...files){
        for (FileEntity file : files) fileContainer.pushBack(file);
        setModificationDate();
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
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

    public LocalDate getModificationDate() {
        return super.getModificationDate();
    }

    private void setModificationDate(){
        super.setModificationDate(LocalDate.now());
    }

    public FileTypes getFileType() {
        return super.getFileTypes();
    }


}
