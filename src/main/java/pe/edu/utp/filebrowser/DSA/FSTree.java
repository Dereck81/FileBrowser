package pe.edu.utp.filebrowser.DSA;

import pe.edu.utp.filebrowser.FileSystem.FileEntity;
import pe.edu.utp.filebrowser.Enums.FileTypes;
import pe.edu.utp.filebrowser.FileSystem.RootItem;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import pe.edu.utp.filebrowser.FileSystem.Path;

/**
 * FileSystemTree
 * Represents a file system tree structure with operations for managing files and directories.
 */
public class FSTree implements Serializable {

    private FileNode root;
    private final HashMap<String, FileNode> lookupTable;

    /**
     * Constructs an empty file system tree with a root node representing "My PC".
     */
    public FSTree() {
        root = new FileNode(
                new RootItem("My PC", FileTypes.PC)
        );
        lookupTable = new HashMap<>();
        lookupTable.put(root.getFile().getPath().toString(), root);
    }

    // region [FILE MANIPULATION METHODS]

    /**
     * Returns the root node of the file system tree.
     *
     * @return the root FileNode
     */
    public FileNode getRoot() {
        return root;
    }

    /**
     * Adds a new FileEntity to the file system tree.
     *
     * @param file the FileEntity to add
     * @return true if the file was added successfully, otherwise false
     */
    public boolean push(FileEntity file) {
        //FileNode parentNode = getNode(file.getDirectoryPath());
        FileNode parentNode = lookupTable.get(file.getDirectoryPath().toString());
        if (parentNode == null)
            return false;

        FileNode child = parentNode.pushChildren(file);

        if (child == null)
            return false;

        lookupTable.put(file.getPath().toString(), child);
        return true;
    }

    /**
     * Removes a FileEntity and its subtree from the file system tree.
     *
     * @param file the FileEntity to remove
     */
    public void remove(FileEntity file){
        FileNode parentNode = lookupTable.get(file.getDirectoryPath().toString());
        if(parentNode == null)
            return;

        FileNode childNode = lookupTable.get(file.getPath().toString());
        if(childNode == null) return;
        parentNode.deleteSubtree(childNode);
        removeLookupTable(childNode.getFile().getPath().toString());
    }

    /**
     * Removes and returns a FileNode from the file system tree.
     *
     * @param fe the FileEntity to remove
     * @return the removed FileNode, or null if it was not found or not removed
     */
    private FileNode pop(FileEntity fe){
        FileNode childNode = lookupTable.get(fe.getPath().toString());
        if(childNode == null) return null;
        remove(fe);
        if(lookupTable.get(fe.getPath().toString()) != null) return null;
        return childNode;
    }

    /**
     * Moves a FileEntity to a new directory.
     *
     * @param fe the FileEntity to move
     * @param dest the destination directory path
     * @return true if the file was moved successfully, otherwise false
     */
    public boolean move(FileEntity fe, Path dest){
        FileNode fn = lookupTable.get(fe.getPath().toString());
        FileNode oldParentNode = lookupTable.get(fe.getDirectoryPath().getPath());
        if(fn == null || oldParentNode == null) return false;
        oldParentNode.deleteSubtree(fn);

        String pathOld = fn.getFile().getPath().toString();
        FileNode newParentNode = lookupTable.get(dest.getPath());

        if(newParentNode == null){
            fn.getFile().setFileEntityParent(oldParentNode.getFile());
            return false;
        }

        fn.getFile().setFileEntityParent(newParentNode.getFile());
        if(!newParentNode.pushChildren(fn)){
            fn.getFile().setFileEntityParent(oldParentNode.getFile());
            return false;
        }
        updateLookupTable(pathOld);
        return true;
    }

    /**
     * Copies a FileEntity and its subtree to a new directory.
     *
     * @param fe the FileEntity to copy
     * @param dest the destination directory path
     * @return true if the file was copied successfully, otherwise false
     */
    public boolean copy(FileEntity fe, Path dest){
        FileNode fn = lookupTable.get(fe.getPath().toString());
        if(fn == null) return false;

        FileNode newParentNode = lookupTable.get(dest.getPath());

        if(newParentNode == null) return false;

        cloneSubtreeAndInsert(fn, newParentNode);

        return true;
    }

    /**
     * Recursively clones a subtree and inserts it into the specified parent node.
     *
     * @param fn the FileNode to clone
     * @param parent the parent node to insert the cloned subtree into
     */
    private void cloneSubtreeAndInsert(FileNode fn, FileNode parent){
        FileEntity clonedFileEntity = fn.getFile().deepCopy();
        clonedFileEntity.setFileEntityParent(parent.getFile());

        if (push(clonedFileEntity)) {
            FileNode clonedNode = lookupTable.get(clonedFileEntity.getPath().toString());

            for (FileNode child : fn.getChildren()) {
                cloneSubtreeAndInsert(child, clonedNode);
            }
        }
    }

    // endregion

    // region [NODE SEARCH METHODS]

    /**
     * Searches for a node by its name in a given branch.
     *
     * @param branch the branch to search within
     * @param name the name of the node to search for
     * @return the found FileNode, or null if not found
     */
    private FileNode searchNode(FileNode branch, String name) {
        // This function needs to be analyzed and fixed
        FileNode node = lookupTable.get(name);
        if(node == null) return null;
        if (node.getFile().getFileType() != FileTypes.PLAINTEXT)
            return node;
        return null;
    }

    /**
     * Retrieves a node by its directory path.
     *
     * @param directoryPath the path of the directory
     * @return the FileNode at the specified path, or null if not found
     */
    public FileNode getNode(Path directoryPath){
        return lookupTable.get(directoryPath.getPath());
    }

    /**
     * Recursively retrieves a node by its directory path.
     *
     * @param directoryPath the path of the directory
     * @param branch the branch to start the search from
     * @return the FileNode at the specified path, or null if not found
     */
    private FileNode getNodeRecursively(Path directoryPath, FileNode branch){
        Path dp;

        if(directoryPath == null || directoryPath.getNameCount() == 0)
            return root;

        if(branch == null)
            return null;

        if(directoryPath.getNameCount() == 1)
            return searchNode(branch, directoryPath.getName(0).toString()); //CHECK!!
        else if(directoryPath.getNameCount() == 2)
            dp = directoryPath.getName(1);
        else
            dp = directoryPath.subpath(1, directoryPath.getNameCount());

        FileNode node = searchNode(branch, directoryPath.getName(0).toString());
        return getNodeRecursively(dp, node);
    }

    // endregion

    // region [FILE ENTITY RETRIEVAL METHODS]

    /**
     * Retrieves the child FileEntities of a specified FileEntity.
     *
     * @param file the FileEntity to get children for
     * @return an array of child FileEntities, or null if the parent node was not found
     */
    public FileEntity[] getChildFilesEntities(FileEntity file){
        FileNode parentNode = lookupTable.get(file.getPath().toString());
        if(parentNode == null) return null;
        int i = 0;
        FileEntity[] fileEntities = new FileEntity[parentNode.subtreeSize()];
        for (FileNode childNode : parentNode.getChildren()) fileEntities[i++] = childNode.getFile();
        return fileEntities;
    }

    /**
     * Retrieves the FileEntities in a specified directory.
     *
     * @param directoryPath the path of the directory
     * @return an array of FileEntities in the directory, or null if the directory node was not found
     */
    public FileEntity[] getFilesEntitiesInDirectory(Path directoryPath){
        FileNode parentNode = lookupTable.get(directoryPath.getPath());
        if(parentNode == null) return null;
        int i = 0;
        FileEntity[] fileEntities = new FileEntity[parentNode.subtreeSize()];
        for (FileNode childNode : parentNode.getChildren())
            fileEntities[i++] = childNode.getFile();
        return fileEntities;
    }

    /**
     * Retrieves a FileEntity by its path.
     *
     * @param path the path of the FileEntity
     * @return the FileEntity at the specified path, or null if not found
     */
    public FileEntity getFileEntityByPath(Path path){
        FileNode node = lookupTable.get(path.getPath());
        if(node == null) return null;
        return node.getFile();
    }

    // endregion

    // region [FILE ENTITY UPDATE METHODS]

    /**
     * Updates the name of a specified FileEntity.
     *
     * @param fe the FileEntity to rename
     * @param newName the new name of the FileEntity
     */
    public void updateFilename(FileEntity fe, String newName) {
        FileNode nd = lookupTable.get(fe.getPath().toString());
        String oldNamePath = fe.getPath().toString();
        FileEntity fe_ = nd.getFile();
        fe_.setName(newName);
        updateLookupTable(oldNamePath);
    }

    /**
     * Updates the entries in the lookup table based on a specified old path.
     *
     * @param oldPath the old path to match and update
     */
    private void updateLookupTable(String oldPath){
        Predicate<String> condition = createPathMatchingPredicate(oldPath);
        KeyUpdater<String, FileNode> updater = (_, value) -> value.getFile().getPath().toString();
        lookupTable.update(condition, updater);
    }

    /**
     * Removes entries from the lookup table based on a specified old path.
     *
     * @param oldPath the old path to match and remove
     */
    private void removeLookupTable(String oldPath){
        Predicate<String> condition = createPathMatchingPredicate(oldPath);
        lookupTable.remove(condition);
    }

    /**
     * Creates a predicate that matches keys in the lookup table based on a specified old path.
     *
     * @param oldPath the old path to match
     * @return a predicate matching keys in the lookup table
     */
    private Predicate<String> createPathMatchingPredicate(String oldPath){
        String escapedOldPath = Pattern.quote(oldPath);
        String regex = "^" + escapedOldPath + "("+Path.separatorToUseRgx+".*)?$";
        return key -> key.matches(regex);
    }

    // endregion

    // region [TRAVERSAL METHODS]

    /**
     * Performs a depth-first traversal of the file system tree, invoking a consumer for each node.
     *
     * @param fileNodeConsumer the consumer to invoke for each FileNode
     * @param current FileNode current
     */
    private void depthFirst(Consumer<FileNode> fileNodeConsumer, FileNode current) {
        if (current.getChildren().size() == 0)
            return;
        for (FileNode x : current.getChildren())
            fileNodeConsumer.accept(x);
        fileNodeConsumer.accept(current);
    }

    /**
     * Performs a depth-first traversal of the file system tree, invoking a consumer for each node.
     *
     * @param fileNodeConsumer the consumer to invoke for each FileNode
     */
    public void depthFirst(Consumer<FileNode> fileNodeConsumer) {
        depthFirst(fileNodeConsumer, root);
    }

    // endregion
}
