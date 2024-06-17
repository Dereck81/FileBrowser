package pe.edu.utp.filebrowser.TreeAndTable;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import pe.edu.utp.filebrowser.FileBrowser;
import pe.edu.utp.filebrowser.FileSystem.FileEntity;
import pe.edu.utp.filebrowser.FileSystem.FileTypes;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class RootItem extends FileEntity {
    private String name;
    private FileTypes fileType;

    public RootItem(String name, FileTypes fileType) {
        super(null, null, Path.of("\\"), null);
        this.name = name;
        this.fileType = fileType;
    }


    public Pane getPane(){
        String path = null;
        if (fileType == FileTypes.DIRECTACCESS) path = "imgs/da.png";
        else if (fileType == FileTypes.PC) path = "imgs/pc.png";

        Pane pane = new Pane();
        Label name = new Label(this.name);

        name.setLayoutX(25);

        if(path != null) {
            File file = new File(Objects
                    .requireNonNull(FileBrowser.class.getResource(path))
                    .getPath().substring(1));
            ImageView imageView = new ImageView(file.toURI().toString());
            imageView.setFitWidth(15);
            imageView.setFitHeight(15);
            imageView.setX(5);
            pane.getChildren().addAll(name, imageView);
        }else pane.getChildren().add(name);

        return pane;
    }

}
