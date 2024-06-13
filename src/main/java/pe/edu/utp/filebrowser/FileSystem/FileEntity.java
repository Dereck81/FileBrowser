package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import pe.edu.utp.filebrowser.FileBrowser;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FileEntity {

    private FileTypes fileTypes;
    private String path;
    private LocalDateTime modificationDate;
    //private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public FileEntity(FileTypes type, String path, LocalDateTime modificationDate) {
        this.fileTypes = type;
        this.path = path;
        this.modificationDate = modificationDate;
    }

    public void setFileType(FileTypes type) {
        fileTypes = type;
    }

    public FileTypes getFileType() {
        return fileTypes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    Pane getPane(String nameF){
        Pane pane = new Pane();
        ImageView imageView = getImageView();
        Label name = new Label(nameF);

        //
        name.setLayoutX(25);
        imageView.setFitWidth(15);
        imageView.setFitHeight(15);
        imageView.setX(5);


        pane.getChildren().addAll(imageView, name);
        return pane;
    }

}
