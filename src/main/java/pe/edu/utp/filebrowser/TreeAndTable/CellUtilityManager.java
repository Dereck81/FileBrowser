package pe.edu.utp.filebrowser.TreeAndTable;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

/**
 * Utility manager for handling selected cells in TableView and TreeView.
 */
public class CellUtilityManager {

    /**
     * Handles mouse pressed events on selected cells in TableView or TreeView.
     *
     * @param tv    The TableView or TreeView instance.
     * @param event The MouseEvent instance representing the mouse press event.
     * @param <T>   The type of TableView or TreeView.
     * @return true if the selected cell was pressed, false otherwise.
     */
    public static <T> boolean handleSelectedCellPressed(T tv, MouseEvent event){
        if(tv instanceof TableView)
            return handleSelectedCellPressedTableView((TableView<?>) tv, event);
        else if (tv instanceof TreeView)
            return handleSelectedCellPressedTreeView((TreeView<?>) tv, event);
        return false;
    }

    /**
     * Handles mouse pressed events on selected cells in a TableView.
     *
     * @param tv    The TableView instance.
     * @param event The MouseEvent instance representing the mouse press event.
     * @return true if the selected cell was pressed, false otherwise.
     */
    private static boolean handleSelectedCellPressedTableView(TableView<?> tv, MouseEvent event){
        int idxSelected = tv.getSelectionModel().getSelectedIndex();
        TableCell<?, ?> source = getTableCell(event.getTarget());
        if(source == null || idxSelected == -1) return false;
        return source.getTableRow().getIndex() == idxSelected;
    }

    /**
     * Handles mouse pressed events on selected cells in a TreeView.
     *
     * @param tv    The TreeView instance.
     * @param event The MouseEvent instance representing the mouse press event.
     * @return true if the selected cell was pressed, false otherwise.
     */
    private static boolean handleSelectedCellPressedTreeView(TreeView<?> tv, MouseEvent event){
        int idxSelected = tv.getSelectionModel().getSelectedIndex();
        TreeCell<?> source = getTreeCell(event.getTarget());
        if(source == null || idxSelected == -1) return false;
        return source.getIndex() == idxSelected;
    }

    /**
     * Retrieves the TableCell associated with the EventTarget recursively.
     *
     * @param eventTarget The EventTarget instance from which to retrieve the TableCell.
     * @return The TableCell associated with the EventTarget, or null if not found.
     */
    public static TableCell<?, ?> getTableCell(EventTarget eventTarget){
        Object element = getTableCellRecursively((Node) eventTarget);
        if(element instanceof TableCell) return (TableCell<?, ?>) element;
        return null;
    }

    /**
     * Retrieves the TreeCell associated with the EventTarget recursively.
     *
     * @param eventTarget The EventTarget instance from which to retrieve the TreeCell.
     * @return The TreeCell associated with the EventTarget, or null if not found.
     */
    public static TreeCell<?> getTreeCell(EventTarget eventTarget){
        Object element = getTreeCellRecursively((Node) eventTarget);
        if(element instanceof TreeCell) return (TreeCell<?>) element;
        return null;
    }

    /**
     * Recursively retrieves the TableCell associated with the given Node.
     *
     * @param element The Node from which to start searching for the TableCell.
     * @return The TableCell associated with the Node, or null if not found.
     */
    private static Node getTableCellRecursively(Node element){
        if(element == null) return null;
        if(element instanceof TableCell) return element;
        return getTableCellRecursively(element.getParent());
    }

    /**
     * Recursively retrieves the TreeCell associated with the given Node.
     *
     * @param element The Node from which to start searching for the TreeCell.
     * @return The TreeCell associated with the Node, or null if not found.
     */
    private static Node getTreeCellRecursively(Node element){
        if(element == null) return null;
        if(element instanceof TreeCell) return element;
        return getTreeCellRecursively(element.getParent());
    }

}
