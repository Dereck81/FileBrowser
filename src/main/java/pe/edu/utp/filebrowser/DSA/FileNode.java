package pe.edu.utp.filebrowser.DSA;

import pe.edu.utp.filebrowser.FileSystem.FileEntity;
import pe.edu.utp.filebrowser.Enums.FileTypes;

import java.io.Serializable;

public class FileNode implements Serializable {
    FileEntity data;
    DynamicArray<FileNode> children;

    public FileNode(FileEntity data) {
        this.data = data;
        this.children = new DynamicArray<>();
    }

    public FileEntity getFile() {
        return data;
    }

    public DynamicArray<FileNode> getChildren() {
        return children;
    }

    /**
     * Recursively checks if a given FileEntity is contained on the subtree defined by *this* node.
     * @param target
     * @return true if the file is contained in the subtree, otherwise false.
     */
    public boolean contains(FileEntity target) {
        if (data.equals(target))
            return true;

        for (FileNode fn : children)
            if (fn.contains(target))
                return true;

        return false;
    }

    public boolean pushChildren(FileEntity child) {

        if (this.data.getFileType() == FileTypes.PLAINTEXT)
            return false;

        if (contains(child))
            return false;

        FileNode nd = new FileNode(child);
        this.children.pushBack(nd);

        return true;
    }

    /**
     * Deletes the subtree initiated by a given child of this node.
     * @param subtreeRoot
     */
    public void deleteSubtree(FileNode subtreeRoot) {

        /**
         * TODO: Handle the special case where the subtree is a folder/disk so
         * the user gets warned about deleting the entire contents of it.
         */
        if (subtreeRoot.data.getFileType() == FileTypes.FOLDER) {
            System.err.println("Given file is a directory.");
        }

        children.delete(children.find(subtreeRoot));
    }

    public int subtreeSize() {
        return children.size();
    }
}