package pe.edu.utp.filebrowser.TreeAndTable;

import javafx.scene.control.TableCell;
import javafx.scene.control.TreeCell;
import pe.edu.utp.filebrowser.FileSystem.*;

/**
 * CellFactory
 * Provides cell factories for JavaFX tree and table views to display FileEntity items.
 */
public class CellFactory {

    /**
     * Sets a custom cell factory for a TreeView of FileEntity items.
     *
     * @return A TreeCell configured with custom rendering based on the type of FileEntity.
     */
    public static TreeCell<FileEntity> setCellFactoryTree() {
        return new TreeCell<>() {
            @Override
            public void updateItem(FileEntity item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null)
                    setGraphic(null);
                else
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

    /**
     * Sets a custom cell factory for a TableView of FileEntity items.
     *
     * @param <T> The type of data in the TableCell.
     * @return A TableCell configured with custom rendering based on the type of FileEntity.
     */
    public static <T> TableCell<FileEntity, T> setCellFactoryTable() {
        return new TableCell<>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                }else
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

