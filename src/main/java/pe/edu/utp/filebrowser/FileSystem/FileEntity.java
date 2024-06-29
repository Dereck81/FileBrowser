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

public class FileEntity implements Comparable<FileEntity>, Serializable {

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
        return directoryPath.resolve(name);
    }

    public Path getDirectoryPath(){
        //example: "Disk D/folder1"
        return directoryPath;
    }

    public void setDirectoryPath(Path directoryPath){
        this.directoryPath = directoryPath;
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

    Pane getPane(){
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

    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    @Override
    public int compareTo(FileEntity o) {
        if ( (directoryPath == null && o.getDirectoryPath() == null)
                || (directoryPath.equals(o.getDirectoryPath())))
            if (name.equals(o.getName()))
                return 0; // comment this line if the following lines are not commented
                //if(fileTypes == o.getFileType()) return 0;
                //else return -1;
                //
                //commented line, because the ext4 file system
                // is not being used.
            else return -1;
        return -1;
    }

    @Override
    public String toString() {
        return name;
    }
}
