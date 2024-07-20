package pe.edu.utp.filebrowser.FileSystem;

import java.io.Serial;
import java.io.Serializable;
import pe.edu.utp.filebrowser.Enums.FileTypes;
import java.time.LocalDateTime;

/**
 * Represents a direct access or shortcut to another FileEntity.
 */
public class DirectAccess extends FileEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2702526546868198172L;
    private FileEntity targetFile;

    public DirectAccess(String shorcutName, Path shortcutPath,
                        FileEntity targetFile) {

        super(shorcutName, targetFile.getFileType(), shortcutPath, LocalDateTime.now());
        this.targetFile = targetFile;
        super.setFileType(getFileTypeTarget());
    }

    public DirectAccess(String shorcutName, Path shortcutPath,
                        FileEntity targetFile, LocalDateTime modificationDate, LocalDateTime creationDate) {

        super(shorcutName, targetFile.getFileType(), shortcutPath, modificationDate, creationDate);
        this.targetFile = targetFile;
        super.setFileType(getFileTypeTarget());
    }

    public DirectAccess(String shortcutName, FileEntity fileEntityParent, FileEntity targetFile) {
        super(shortcutName, targetFile.getFileType(), fileEntityParent, LocalDateTime.now());
        this.targetFile = targetFile;
        super.setFileType(getFileTypeTarget());
    }

    public DirectAccess(String shortcutName, FileEntity fileEntityParent, FileEntity targetFile,
                        LocalDateTime modificationDate, LocalDateTime creationDate) {
        super(shortcutName, targetFile.getFileType(), fileEntityParent, modificationDate, creationDate);
        this.targetFile = targetFile;
        super.setFileType(getFileTypeTarget());
    }

    public Path getShortcutPath() {
        return getShortCutPathTarget(); // ???
    }

    public void setShortcutFileParent(FileEntity fileEntityParent) {
        super.setFileEntityParent(fileEntityParent);
    }

    public Path getShortcutDirectoryPath(){
        return super.getDirectoryPath();
    }

    public Path getShortCutPathTarget(){
        return getFinalTargetFile(targetFile).getPath();
    }

    public Path getShortCutDirectoryPathTarget(){
        return getFinalTargetFile(targetFile).getDirectoryPath();
    }

    public int getSize(){
        if (getTargetFile() instanceof PlainText)
            return getTargetFile().getSize();
        else return 0;
    }

    /*
    There has to be a way for it to get into the directory or file it points to.
     */
    public FileEntity getTargetFile() {
        return getFinalTargetFile(targetFile);
    }

    public void setTargetFile(FileEntity targetFile) {
        this.targetFile = targetFile;
        setModificationDate();
    }

    private void setModificationDate() {
        super.setModificationDate(LocalDateTime.now());
    }

    public FileTypes getFileType() {
        return super.getFileType();
    }

    private FileTypes getFileTypeTarget(){
        return switch (targetFile.getFileType()){
            case FOLDER, DIRECTACCESS_FOLDER -> FileTypes.DIRECTACCESS_FOLDER;
            case PLAINTEXT, DIRECTACCESS_PLAINTEXT -> FileTypes.DIRECTACCESS_PLAINTEXT;
            case VIRTUALDISK, DIRECTACCESS_VIRTUALDISK -> FileTypes.DIRECTACCESS_VIRTUALDISK;
            default -> null;
        };
    }

    private FileEntity getFinalTargetFile(FileEntity feTarget){
        if(!(feTarget instanceof DirectAccess)) return feTarget;

        return getFinalTargetFile((((DirectAccess) feTarget).getTargetFile()));
    }

}
