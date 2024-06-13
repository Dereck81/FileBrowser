package pe.edu.utp.filebrowser.FileSystem;


import java.time.LocalDate;

public class DirectAccess extends FileEntity {
    private String shorcutName;
    private FileEntity fileTarget;

    public DirectAccess(String shorcutName, String shortcutPath,
                        FileEntity fileTarget) {
        super(FileTypes.DIRECTACCESS, shortcutPath, LocalDate.now());
        this.shorcutName = shorcutName;
        this.fileTarget = fileTarget;
    }

    public String getShorcutName() {
        return shorcutName;
    }

    public void setShorcutName(String shorcutName) {
        this.shorcutName = shorcutName;
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

    public LocalDate getModificationDate(){
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


    public FileTypes getFileType() {
        return super.getFileTypes();
    }

}
