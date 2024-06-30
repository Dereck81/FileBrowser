package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.layout.Pane;

import java.io.Serializable;

import pe.edu.utp.filebrowser.Enums.FileTypes;

import java.time.LocalDateTime;

public class PlainText extends FileEntity implements Serializable {

    private StringBuilder content = new StringBuilder();

    //public PlainText(String fileName, Path filePath) {
    //    super(fileName, FileTypes.PLAINTEXT, filePath, LocalDateTime.now());
    //}

    public PlainText(String fileName, FileEntity fileEntityParent) {
        super(fileName, FileTypes.PLAINTEXT, fileEntityParent, LocalDateTime.now());
    }


    public int getSize(){
        return content.toString().getBytes().length;
    }

    private void setModificationDate() {
        super.setModificationDate(LocalDateTime.now());
    }

    public void setContent(String content){
        this.content.append(content);
        setModificationDate();
    }

    public String getContent(){
        return content.toString();
    }


}
