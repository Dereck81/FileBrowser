package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import pe.edu.utp.filebrowser.Enums.FileTypes;
import pe.edu.utp.filebrowser.FileBrowser;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a generic file system entity.
 */
public class FileEntity implements Comparable<FileEntity>, Serializable {

    private FileEntity fileEntityParent;
    private FileTypes fileTypes;
    private Path directoryPath;
    private LocalDateTime modificationDate;
    private final LocalDateTime creationDate;
    private String name;
    //private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public FileEntity(String name, FileTypes type, Path directoryPath, LocalDateTime modificationDate) {
        this.fileTypes = type;
        this.modificationDate = modificationDate;
        this.creationDate = modificationDate;
        this.name = name;
        this.directoryPath = directoryPath;
        this.fileEntityParent = null;
    }

    public FileEntity(String name, FileTypes type, Path directoryPath,
                      LocalDateTime modificationDate, LocalDateTime creationDate) {
        this.fileTypes = type;
        this.modificationDate = modificationDate;
        this.creationDate = creationDate;
        this.name = name;
        this.directoryPath = directoryPath;
        this.fileEntityParent = null;
    }

    public FileEntity(String name, FileTypes type, FileEntity feParent, LocalDateTime modificationDate) {
        this.fileTypes = type;
        this.modificationDate = modificationDate;
        this.creationDate = modificationDate;
        this.name = name;
        this.fileEntityParent = feParent;
        this.directoryPath = null;
    }

    public FileEntity(String name, FileTypes type, FileEntity feParent,
                      LocalDateTime modificationDate, LocalDateTime creationDate) {
        this.fileTypes = type;
        this.modificationDate = modificationDate;
        this.creationDate = creationDate;
        this.name = name;
        this.fileEntityParent = feParent;
        this.directoryPath = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setFileType(FileTypes type) {
        fileTypes = type;
    }

    public FileTypes getFileType() {
        return fileTypes;
    }

    public Path getPath() {
        //example: "Disk D/folder1/filename"
        if(this.fileEntityParent != null)
            return this.fileEntityParent.getPath().resolve(this.name);
        return directoryPath.resolve(name);
    }

    public Path getDirectoryPath(){
        //example: "Disk D/folder1"
        if(this.fileEntityParent != null)
            return this.fileEntityParent.getPath();
        return directoryPath;
    }

    public void setFileEntityParent(FileEntity fileEntityParent){
        this.fileEntityParent = fileEntityParent;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    /**
     * It is based on the Windows file manager,
     * which only modifies the date when a file is added
     * and not when its name or path is modified.
     */
    void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public LocalDateTime getCreationDate(){
        return creationDate;
    }

    public int getSize(){
        return 0;
    }

    /**
     * Retrieves an ImageView representing the icon of this file entity.
     *
     * @return An ImageView representing the icon of this file entity.
     */
    public ImageView getImageView() {
        String PATH = switch (fileTypes) {
            case FOLDER -> "imgs/folder.png";
            case PLAINTEXT -> "imgs/file.png";
            case VIRTUALDISK -> "imgs/virtualdisk.png";
            case DIRECTACCESS_FOLDER -> "imgs/folder_da.png";
            case DIRECTACCESS_PLAINTEXT -> "imgs/file_da.png";
            case DIRECTACCESS_VIRTUALDISK -> "imgs/virtualdisk_da.png";
            default -> null; // no delete
        };
        if (PATH == null) return null;
        File file = new File(Objects
                .requireNonNull(FileBrowser.class.getResource(PATH))
                .getPath().substring(1));

        return new ImageView(file.toURI().toString());
    }

    /**
     * Constructs a Pane displaying this file entity with an icon and name.
     *
     * @return A Pane displaying this file entity.
     */
    public Pane getPane(){
        Pane pane = new Pane();
        ImageView imageView = getImageView();
        Label name = new Label(this.name);

        //
        name.setLayoutX(25);
        imageView.setFitWidth(15);
        imageView.setFitHeight(15);
        imageView.setX(5);

        pane.getChildren().addAll(imageView, name);
        return pane;
    }

    /**
     * Compares this file entity with another file entity for ordering.
     * File entities are ordered primarily by directory path and then by name.
     *
     * @param o The file entity to be compared.
     * @return A negative integer, zero, or a positive integer as this file entity
     *           is less than, equal to, or greater than the specified file entity.
     */
    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    @Override
    public int compareTo(FileEntity o) {
        if ((getDirectoryPath() == null && o.getDirectoryPath() == null)
                || (getDirectoryPath().equals(o.getDirectoryPath()))) {

            if (name == null || o.getName() == null)
                return -1;

            if (name.equals(o.getName()))
                return 0; // comment this line if the following lines are not commented
                //if(fileTypes == o.getFileType()) return 0;
                //else return -1;
                //
                //commented line, because the ext4 file system
                // is not being used.
            else return -1;
        }
        return -1;
    }

    /**
     * Creates a deep copy of this file entity.
     *
     * @return A deep copy of this file entity.
     */
    public FileEntity deepCopy(){
        switch (this) {
            case VirtualDiskDriver _ -> {
                return new VirtualDiskDriver(name, directoryPath, modificationDate, creationDate);
            }
            case Folder _ -> {
                return new Folder(name, fileEntityParent, modificationDate, creationDate);
            }
            case PlainText plainText -> {
                return new PlainText(name, fileEntityParent, plainText.getContent(), modificationDate, creationDate);
            }
            case DirectAccess _ -> {
                if (fileEntityParent == null)
                    return new DirectAccess(name, directoryPath, ((DirectAccess) this).getTargetFile(),
                            modificationDate, creationDate);
                else
                    return new DirectAccess(name, fileEntityParent, ((DirectAccess) this).getTargetFile(),
                            modificationDate, creationDate);
            }
            case RootItem rootItem -> {
                return new RootItem(name, rootItem.getFileTypes(), modificationDate, creationDate);
            }
            default -> {}
        }
        if(fileEntityParent == null)
            return new FileEntity(name, fileTypes, directoryPath, modificationDate, creationDate);
        return new FileEntity(name, fileTypes, fileEntityParent, modificationDate, creationDate);
    }

    @Override
    public String toString() {
        return name;
    }

}
