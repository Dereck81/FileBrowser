package pe.edu.utp.filebrowser.DSA;

import pe.edu.utp.filebrowser.FileSystem.FileEntity;
import pe.edu.utp.filebrowser.Enums.FileTypes;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a node in a file system tree structure.
 * Each node contains a FileEntity and a list of children nodes.
 */
public class FileNode implements Serializable {

    @Serial
    private static final long serialVersionUID = 5030174569902236442L;
    FileEntity data;
    DynamicArray<FileNode> children;

    /**
     * Constructs a FileNode with the specified FileEntity.
     *
     * @param data the FileEntity associated with this node
     */
    public FileNode(FileEntity data) {
        this.data = data;
        this.children = new DynamicArray<>();
    }

    /**
     * Returns the FileEntity associated with this node.
     *
     * @return the FileEntity associated with this node
     */
    public FileEntity getFile() {
        return data;
    }

    /**
     * Returns the children of this node.
     *
     * @return the children of this node
     */
    public DynamicArray<FileNode> getChildren() {
        return children;
    }

    /**
     * Recursively checks if a given FileEntity is contained on the subtree defined by *this* node.
     * @param target the FileEntity to check for
     * @return true if the file is contained in the subtree, otherwise false.
     */
    public boolean contains(FileEntity target) {
        if (data.compareTo(target) == 0)
            return true;

        for (FileNode fn : children)
            if (fn.contains(target))
                return true;

        return false;
    }

    /**
     * Adds a new child FileEntity to this node.
     *
     * @param child the FileEntity to add as a child
     * @return the newly created FileNode if the child was added successfully, otherwise null
     */
    public FileNode pushChildren(FileEntity child) {

        if (this.data.getFileType() == FileTypes.PLAINTEXT)
            return null;

        if (contains(child))
            return null;

        FileNode nd = new FileNode(child);
        this.children.pushBack(nd);

        return nd;
    }

    /**
     * Adds an existing FileNode as a child of this node.
     *
     * @param child the FileNode to add as a child
     * @return true if the child was added successfully, otherwise false
     */
    public boolean pushChildren(FileNode child){
        if (this.data.getFileType() == FileTypes.PLAINTEXT)
            return false;

        if (contains(child.getFile()))
            return false;

        this.children.pushBack(child);

        return true;
    }

    /**
     * Deletes the subtree initiated by a given child of this node.
     * @param subtreeRoot the root of the subtree to delete
     */
    public void deleteSubtree(FileNode subtreeRoot) {
        /*
         * TODO: Handle the special case where the subtree is a folder/disk so
         * the user gets warned about deleting the entire contents of it.
         */
        if (subtreeRoot.data.getFileType() == FileTypes.FOLDER) {
            System.err.println("Given file is a directory.");
        }

        children.delete(children.find(subtreeRoot));
    }

    /**
     * Returns the size of the subtree defined by this node.
     *
     * @return the size of the subtree
     */
    public int subtreeSize() {
        return children.size();
    }

}