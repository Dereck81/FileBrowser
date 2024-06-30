package pe.edu.utp.filebrowser.DSA;

import pe.edu.utp.filebrowser.FileSystem.FileEntity;
import pe.edu.utp.filebrowser.Enums.FileTypes;
import pe.edu.utp.filebrowser.FileSystem.RootItem;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import pe.edu.utp.filebrowser.FileSystem.Path;

/**
 * FileSystemTree
 */
public class FSTree implements Serializable {

    private FileNode root;
    private final HashMap<String, FileNode> lookupTable;

    public FSTree() {
        root = new FileNode(
                new RootItem(null, null)
        );
        lookupTable = new HashMap<>();
        lookupTable.put(root.getFile().getPath().toString(), root);
    }

    public boolean push(FileEntity file) {
        //FileNode parentNode = getNode(file.getDirectoryPath());
        FileNode parentNode = lookupTable.get(file.getDirectoryPath().toString());
        if (parentNode == null)
            return false;

        if(lookupTable.get(file.getPath().toString()) != null)
            return false; // file with the same name and path exists

        FileNode child = parentNode.pushChildren(file);

        if (child == null)
            return false;

        lookupTable.put(file.getPath().toString(), child);
        return true;
    }

    public void remove(FileEntity file){
        //FileNode parentNode = getNode(file.getDirectoryPath());
        FileNode parentNode = lookupTable.get(file.getDirectoryPath().toString());
        if(parentNode == null)
            return;

        FileNode childNode = lookupTable.get(file.getPath().toString());
        if(childNode == null) return;
        parentNode.deleteSubtree(childNode);
        removeLookupTable(childNode.getFile().getPath().toString());
    }

    private FileNode pop(FileEntity fe){
        FileNode childNode = lookupTable.get(fe.getPath().toString());
        if(childNode == null) return null;
        remove(fe);
        if(lookupTable.get(fe.getPath().toString()) != null) return null;
        return childNode;
    }

    public boolean move(FileEntity fe, Path dest){
        FileNode fn = lookupTable.get(fe.getPath().toString());
        FileNode oldParentNode = lookupTable.get(fe.getDirectoryPath().getPath());
        if(fn == null || oldParentNode == null) return false;
        oldParentNode.deleteSubtree(fn);

        String pathOld = fn.getFile().getPath().toString();
        FileNode newParentNode = lookupTable.get(dest.getPath());
        fn.getFile().setFileEntityParent(newParentNode.getFile());
        if(!newParentNode.pushChildren(fn)){
            fn.getFile().setFileEntityParent(oldParentNode.getFile());
            return false;
        }
        updateLookupTable(pathOld);
        return true;
    }


    private FileNode searchNode(FileNode branch, String name) {
        // This function needs to be analyzed and fixed
        FileNode node = lookupTable.get(name);
        if(node == null) return null;
        if (node.getFile().getFileType() != FileTypes.PLAINTEXT)
            return node;
        return null;
    }

    public FileNode getRoot() {
        return root;
    }

    private FileNode getNode(Path directoryPath){
        return getNodeRecursively(directoryPath, root);
    }

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

    public FileEntity[] getChildFilesEntities(FileEntity file){
        //FileNode parentNode = getNode(file.getDirectoryPath());
        FileNode parentNode = lookupTable.get(file.getPath().toString());
        // if(parentNode instanceof PlainText) return;
        if(parentNode == null) return null;
        int i = 0;
        FileEntity[] fileEntities = new FileEntity[parentNode.subtreeSize()];
        for (FileNode childNode : parentNode.getChildren()) fileEntities[i++] = childNode.getFile();
        return fileEntities;
    }

    public FileEntity[] getFilesEntitiesInDirectory(Path directoryPath){
        //FileNode parentNode = getNode(directoryPath);
        FileNode parentNode = lookupTable.get(directoryPath.getPath());
        // if(parentNode instanceof PlainText) return;
        if(parentNode == null) return null;
        int i = 0;
        FileEntity[] fileEntities = new FileEntity[parentNode.subtreeSize()];
        for (FileNode childNode : parentNode.getChildren())
            fileEntities[i++] = childNode.getFile();
        return fileEntities;
    }

    public FileEntity getFileEntityByPath(Path path){
        //FileNode node = getNode(path);
        FileNode node = lookupTable.get(path.getPath());
        if(node == null) return null;
        return node.getFile();
    }

    public void updateFilename(FileEntity fe, String newName) {
        FileNode nd = lookupTable.get(fe.getPath().toString());
        String oldNamePath = fe.getPath().toString();
        FileEntity fe_ = nd.getFile();
        fe_.setName(newName);
        updateLookupTable(oldNamePath);
        //lookupTable.remove(oldNamePath);
        //lookupTable.put(fe_.getPath().toString(), nd);
    }

    private void updateLookupTable(){

    }

    private void updateLookupTable(String pathOld){
        String escapedPathOld = Pattern.quote(pathOld);
        String regex = "^" + escapedPathOld + "("+Path.separatorToUseRgx+".*)?$";
        Predicate<String> condition = key -> key.matches(regex);
        KeyUpdater<String, FileNode> updater = (_, value) -> value.getFile().getPath().toString();
        lookupTable.update(condition, updater);
    }

    private void removeLookupTable(String pathOld){
        String escapedPathOld = Pattern.quote(pathOld);
        String regex = "^" + escapedPathOld + "("+Path.separatorToUseRgx+".*)?$";
        Predicate<String> condition = key -> key.matches(regex);
        lookupTable.remove(condition);
    }

    private void depthFirst(Consumer<FileNode> fileNodeConsumer, FileNode current) {
        if (current.getChildren().size() == 0)
            return;
        for (FileNode x : current.getChildren())
            fileNodeConsumer.accept(x);
        fileNodeConsumer.accept(current);
    }

    public void depthFirst(Consumer<FileNode> fileNodeConsumer) {
        depthFirst(fileNodeConsumer, root);
    }
}
