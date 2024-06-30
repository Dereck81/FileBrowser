package pe.edu.utp.filebrowser.TreeAndTable;

import javafx.scene.control.TableCell;
import javafx.scene.control.TreeCell;
import pe.edu.utp.filebrowser.FileSystem.*;

public class CellFactory {

    public static TreeCell<FileEntity> setCellFactoryTree() {
        return new TreeCell<>() {
            @Override
            public void updateItem(FileEntity item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null)
                    setGraphic(null);
                else
                    // Set a custom graphic based on the type of the element
                    if (item instanceof RootItem)
                        setGraphic(item.getPane());
                    else if (item instanceof PlainText)
                        setGraphic(item.getPane());
                    else if (item instanceof Folder)
                        setGraphic(item.getPane());
                    else if (item instanceof VirtualDiskDriver)
                        setGraphic(item.getPane());
                    else if (item instanceof DirectAccess)
                        setGraphic(item.getPane());
                }
        };
    }

    public static <T> TableCell<FileEntity, T> setCellFactoryTable() {
        return new TableCell<>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                }else
                    // Set a custom graphic based on the type of the element
                    if (item instanceof PlainText)
                        setGraphic(((PlainText) item).getPane());
                    else if (item instanceof Folder)
                        setGraphic(((Folder) item).getPane());
                    else if (item instanceof VirtualDiskDriver)
                        setGraphic(((VirtualDiskDriver) item).getPane());
                    else if (item instanceof DirectAccess)
                        setGraphic(((DirectAccess) item).getPane());
                    else if (item instanceof String)
                        setText(item.toString());
            }

        };
    }

}

