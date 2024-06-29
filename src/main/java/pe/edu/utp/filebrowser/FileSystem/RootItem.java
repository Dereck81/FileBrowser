package pe.edu.utp.filebrowser.FileSystem;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import pe.edu.utp.filebrowser.Enums.FileTypes;
import pe.edu.utp.filebrowser.FileBrowser;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class RootItem extends FileEntity implements Serializable {
    private String name;
    private FileTypes fileType;

    public RootItem(String name, FileTypes fileType) {
        super(null, null, new Path(Path.separatorToUse), null);
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
