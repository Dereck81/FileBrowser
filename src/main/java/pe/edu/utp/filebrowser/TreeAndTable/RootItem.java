package pe.edu.utp.filebrowser.TreeAndTable;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import pe.edu.utp.filebrowser.FileSystem.FileEntity;

public class RootItem extends FileEntity {
    private String name;

    public RootItem(String name) {
        super(null, null, null, null);
        this.name = name;
    }

    public Pane getPane(){
        Pane pane = new Pane();
        Label name = new Label(this.name);
        pane.getChildren().add(name);
        return pane;
    }

}
