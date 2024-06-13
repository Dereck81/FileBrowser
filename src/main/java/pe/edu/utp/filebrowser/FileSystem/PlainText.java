package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.layout.Pane;

import java.time.LocalDateTime;

public class PlainText extends FileEntity{
    private String name;

    public PlainText(String fileName, String filePath) {
        super(FileTypes.PLAINTEXT, filePath, LocalDateTime.now());
        this.name = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath(){
        return super.getPath();
    }

    public void setPath(String path){
        super.setPath(path);
    }

    public FileTypes getFileType() {
        return super.getFileType();
    }

    public LocalDateTime getModificationDate() {
        return super.getModificationDate();
    }

    public Pane getPane(){
        return super.getPane(name);
    }

}
