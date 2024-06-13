package pe.edu.utp.filebrowser.FileSystem;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.scene.layout.Pane;

public class DirectAccess extends FileEntity {
    private String name;
    private FileEntity fileTarget;
    private FileTypes type;

    public DirectAccess(String shorcutName, String shortcutPath,
                        FileEntity fileTarget) {

        super(fileTarget.getFileType(), shortcutPath, LocalDateTime.now());
        super.setFileType(getFileTypeTarget());
        this.name = shorcutName;
        this.fileTarget = fileTarget;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortcutPath() {
        return super.getPath();
    }

    public void setShortcutPath(String shortcutPath) {
        super.setPath(shortcutPath);
    }

    /*
    There has to be a way for it to get into the directory or file it points to.
     */
    public FileEntity getFileTarget() {
        return fileTarget;
    }

    public void setFileTarget(FileEntity fileTarget) {
        this.fileTarget = fileTarget;
        setModificationDate();
    }

    public LocalDateTime getModificationDate(){
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


    public FileTypes getFileType() {
        return super.getFileType();
    }

    private FileTypes getFileTypeTarget(){
        return switch (fileTarget.getFileType()){
            case FOLDER -> FileTypes.DIRECTACCESS_FOLDER;
            case PLAINTEXT -> FileTypes.DIRECTACCESS_PLAINTEXT;
            case VIRTUALDISK -> FileTypes.DIRECTACCESS_VIRTUALDISK;
            default -> null;
        };
    }

    public Pane getPane(){
        return super.getPane(name);
    }

}
