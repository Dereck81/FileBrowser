package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.layout.Pane;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class PlainText extends FileEntity{
    private StringBuilder content = new StringBuilder();

    public PlainText(String fileName, Path filePath) {
        super(fileName, FileTypes.PLAINTEXT, filePath, LocalDateTime.now());
    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public Path getDirectoryPath(){
        return super.getDirectoryPath();
    }

    public Path getPath(){
        return super.getPath();
    }

    public void setDirectoryPath(Path directoryPath){
        super.setDirectoryPath(directoryPath);
    }

    public FileTypes getFileType() {
        return super.getFileType();
    }

    public LocalDateTime getModificationDate() {
        return super.getModificationDate();
    }

    public void setContent(String content){
        this.content.append(content);
    }

    public String getContent(){
        return content.toString();
    }

    public Pane getPane(){
        return super.getPane();
    }

}
