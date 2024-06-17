package pe.edu.utp.filebrowser.FileSystem;

import java.nio.file.Path;
import java.time.LocalDateTime;

import javafx.scene.layout.Pane;

public class DirectAccess extends FileEntity {
    private FileEntity targetFile;

    public DirectAccess(String shorcutName, Path shortcutPath,
                        FileEntity targetFile) {

        super(shorcutName, targetFile.getFileType(), shortcutPath, LocalDateTime.now());
        this.targetFile = targetFile;
        super.setFileType(getFileTypeTarget());

    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public Path getShortcutPath() {
        return getShortCutPathTarget(); // ???
    }

    public void setShortcutDirectoryPath(Path shortcutPath) {
        super.setDirectoryPath(shortcutPath);
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
        if (getTargetFile() instanceof PlainText){
            return getTargetFile().getSize();
        }else return 0;
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


    public Pane getPane(){
        return super.getPane();
    }

}
