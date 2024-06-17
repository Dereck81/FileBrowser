package pe.edu.utp.filebrowser.TreeAndTable;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

public class CellUtilityManager {

    public static <T> boolean handleSelectedCellPressed(T tv, MouseEvent event){
        if(tv instanceof TableView)
            return handleSelectedCellPressedTableView((TableView<?>) tv, event);
        else if (tv instanceof TreeView)
            return handleSelectedCellPressedTreeView((TreeView<?>) tv, event);
        return false;
    }

    private static boolean handleSelectedCellPressedTableView(TableView<?> tv, MouseEvent event){
        int idxSelected = tv.getSelectionModel().getSelectedIndex();
        TableCell<?, ?> source = getTableCell(event.getTarget());
        if(source == null || idxSelected == -1) return false;
        return source.getTableRow().getIndex() == idxSelected;
    }

    private static boolean handleSelectedCellPressedTreeView(TreeView<?> tv, MouseEvent event){
        int idxSelected = tv.getSelectionModel().getSelectedIndex();
        TreeCell<?> source = getTreeCell(event.getTarget());
        if(source == null || idxSelected == -1) return false;
        return source.getIndex() == idxSelected;
    }

    public static TableCell<?, ?> getTableCell(EventTarget eventTarget){
        Object element = getTableCellRecursively((Node) eventTarget);
        if(element instanceof TableCell) return (TableCell<?, ?>) element;
        return null;
    }

    public static TreeCell<?> getTreeCell(EventTarget eventTarget){
        Object element = getTreeCellRecursively((Node) eventTarget);
        if(element instanceof TreeCell) return (TreeCell<?>) element;
        return null;
    }

    private static Node getTableCellRecursively(Node element){
        if(element == null) return null;
        if(element instanceof TableCell) return element;
        return getTableCellRecursively(element.getParent());
    }

    private static Node getTreeCellRecursively(Node element){
        if(element == null) return null;
        if(element instanceof TreeCell) return element;
        return getTreeCellRecursively(element.getParent());
    }

}
