package pe.edu.utp.filebrowser.FileSystem;

import java.time.LocalDate;

public class PlainText extends FileEntity{
    private String fileName;

    public PlainText(String fileName, String filePath) {
        super(FileTypes.PLAINTEXT, filePath, LocalDate.now());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath(){
        return super.getPath();
    }

    public void setPath(String path){
        super.setPath(path);
    }

    public FileTypes getFileType() {
        return super.getFileTypes();
    }

}
