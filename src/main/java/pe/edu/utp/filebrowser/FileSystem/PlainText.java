package pe.edu.utp.filebrowser.FileSystem;

import java.io.Serial;
import java.io.Serializable;
import pe.edu.utp.filebrowser.Enums.FileTypes;
import java.time.LocalDateTime;

public class PlainText extends FileEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -5774729391732912747L;
    private String content = "";

    public PlainText(String fileName, FileEntity fileEntityParent) {
        super(fileName, FileTypes.PLAINTEXT, fileEntityParent, LocalDateTime.now());
        setName(fileName);
    }

    public PlainText(String fileName, FileEntity fileEntityParent,
                     LocalDateTime modificationDate, LocalDateTime creationDate) {
        super(fileName, FileTypes.PLAINTEXT, fileEntityParent, modificationDate, creationDate);
        setName(fileName);
    }

    public PlainText(String fileName, FileEntity fileEntityParent, String content,
                     LocalDateTime modificationDate, LocalDateTime creationDate) {
        super(fileName, FileTypes.PLAINTEXT, fileEntityParent, modificationDate, creationDate);

        setName(fileName);
        this.content = content;
    }

    private String convertName(String fileName){
        if (!fileName.toLowerCase().endsWith(".txt"))
            return fileName + ".txt";

        return fileName;
    }

    @Override
    public void setName(String name) {
        super.setName(convertName(name));
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
