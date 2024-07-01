package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.layout.Pane;

import java.io.Serializable;

import pe.edu.utp.filebrowser.Enums.FileTypes;

import java.time.LocalDateTime;

public class PlainText extends FileEntity implements Serializable {

    private String content = "";

    public PlainText(String fileName, FileEntity fileEntityParent) {
        super(fileName, FileTypes.PLAINTEXT, fileEntityParent, LocalDateTime.now());
    }

    public PlainText(String fileName, FileEntity fileEntityParent,
                     LocalDateTime modificationDate, LocalDateTime creationDate) {
        super(fileName, FileTypes.PLAINTEXT, fileEntityParent, modificationDate, creationDate);
    }

    public PlainText(String fileName, FileEntity fileEntityParent, String content,
                     LocalDateTime modificationDate, LocalDateTime creationDate) {
        super(fileName, FileTypes.PLAINTEXT, fileEntityParent, modificationDate, creationDate);
        this.content = content;
    }


    public int getSize(){
        return content.getBytes().length;
    }

    private void setModificationDate() {
        super.setModificationDate(LocalDateTime.now());
    }

    public void setContent(String content){
        this.content = content;
        setModificationDate();
    }

    public String getContent(){
        return content;
    }


}
