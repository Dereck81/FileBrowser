package pe.edu.utp.filebrowser.FileSystem;

import java.time.LocalDate;

public class FileEntity {

    private final FileTypes fileTypes;
    private String path;
    private LocalDate modificationDate;

    public FileEntity(FileTypes type, String path, LocalDate modificationDate) {
        this.fileTypes = type;
        this.path = path;
        this.modificationDate = modificationDate;
    }

    public FileTypes getFileTypes() {
        return fileTypes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDate getModificationDate() {
        return modificationDate;
    }

    /**
     * It is based on the Windows file manager,
     * which only modifies the date when a file is added
     * and not when its name or path is modified.
     */
    public void setModificationDate(LocalDate modificationDate) {
        this.modificationDate = modificationDate;
    }

}
