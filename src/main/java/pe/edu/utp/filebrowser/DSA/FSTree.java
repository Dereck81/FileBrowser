package pe.edu.utp.filebrowser.DSA;

import pe.edu.utp.filebrowser.FileSystem.FileEntity;
import pe.edu.utp.filebrowser.Enums.FileTypes;
import pe.edu.utp.filebrowser.FileSystem.RootItem;

import java.io.Serializable;
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
    }

    public boolean push(FileEntity file) {
        FileNode node = getNode(file.getDirectoryPath());
        if (node == null)
            return false;

        if (!node.pushChildren(file))
            return false;

        lookupTable.put(file.getName(), node);
        return true;
    }

    public void remove(FileEntity file){
        FileNode parentNode = getNode(file.getDirectoryPath());

        if(parentNode == null)
            return;

        FileNode childNode = lookupTable.get(file.getName());
        if(childNode == null) return;
        parentNode.deleteSubtree(childNode);
    }

    private FileNode searchNode(FileNode branch, String name) {
        FileNode node = lookupTable.get(name);
        if(node == null) return null;
        if (node.getFile().getFileType() != FileTypes.PLAINTEXT)
            return node;
        return null;
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
            return searchNode(branch, directoryPath.getName(0).toString());
        else if(directoryPath.getNameCount() == 2)
            dp = directoryPath.getName(1);
        else
            dp = directoryPath.subpath(1, directoryPath.getNameCount());

        FileNode node = searchNode(branch, directoryPath.getName(0).toString());
        return getNodeRecursively(dp, node);
    }

    public FileEntity[] getChildFilesEntities(FileEntity file){
        FileNode parentNode = getNode(file.getDirectoryPath());
        if(parentNode == null) return null;
        int i = 0;
        FileEntity[] fileEntities = new FileEntity[parentNode.subtreeSize()];
        for (FileNode childNode : parentNode.getChildren()) fileEntities[i++] = childNode.getFile();
        return fileEntities;
    }

    public FileEntity[] getFilesEntitiesInDirectory(Path directoryPath){
        FileNode parentNode = getNode(directoryPath);
        if(parentNode == null) return null;
        int i = 0;
        FileEntity[] fileEntities = new FileEntity[parentNode.subtreeSize()];
        for (FileNode childNode : parentNode.getChildren())
            fileEntities[i++] = childNode.getFile();
        return fileEntities;
    }

    public FileEntity getFileEntityByPath(Path path){
        FileNode node = getNode(path);
        if(node == null) return null;
        return node.getFile();
    }

    public void updateFilename(FileEntity fe, String newName) {
        FileNode nd = getNode(fe.getPath());
        String oldName = fe.getName();
        FileNode p = lookupTable.get(oldName);
        nd.getFile().setName(newName);
        lookupTable.remove(oldName);
        lookupTable.put(newName, p);
    }

}
