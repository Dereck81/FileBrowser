package pe.edu.utp.filebrowser.DSA;

import pe.edu.utp.filebrowser.FileSystem.FileEntity;
import pe.edu.utp.filebrowser.FileSystem.FileTypes;
import pe.edu.utp.filebrowser.FileSystem.PlainText;
import pe.edu.utp.filebrowser.TreeAndTable.RootItem;

import java.io.Serializable;
import pe.edu.utp.filebrowser.FileSystem.Path;

public class Tree implements Serializable {

    static class Node implements Serializable{
        private final FileEntity fe;
        private final DynamicArray<Node> nodes = new DynamicArray<>();
        private final HashMap<String, Node> fileAssignmentTable = new HashMap<>();

        public Node(FileEntity file) {
            this.fe = file;
        }

        public FileEntity getFile() {
            return fe;
        }

        public void removeNode(Node nodeTarget) {
            nodes.delete(nodes.find(nodeTarget));
            fileAssignmentTable.remove(nodeTarget.getFile().getName()); // Unused hashmap
        }

        public boolean addNode(FileEntity file) {
            //add a way in case of a shortcut
            if(fe instanceof PlainText) return false;
            if(contains(file)) return false;
            Node node = new Node(file);
            fileAssignmentTable.put(file.getName(), node); // Unused hashmap
            nodes.pushBack(node);
            return true;
        }

        public boolean contains(FileEntity file){
            /* // Unused HashMap
                for(Node node: nodes)
                    if(node.getFile().compareTo(file) == 0)
                        return true;
                return false;
             */
            if(fileAssignmentTable.contains(file.getName())){
                return fileAssignmentTable.get(file.getName()).getFile().compareTo(file) == 0;
            }
            return false;
        }

        public int size(){
            return nodes.size();
        }

    }

    private Node root;

    public Tree() {
        root = new Node(new RootItem(null, null));
    }

    public boolean push(FileEntity file) {
        Node node = getNode(file.getDirectoryPath());
        if(node == null) return false;
        return node.addNode(file);
    }

    public void remove(FileEntity file){
        Node parentNode = getNode(file.getDirectoryPath());
        if(parentNode == null) return;
        Node childNode = parentNode.fileAssignmentTable.get(file.getName());
        if(childNode == null) return;
        parentNode.removeNode(childNode);
    }

    private Node searchNode(Node branch, String name){
        Node node = branch.fileAssignmentTable.get(name);
        if(node == null) return null;
        if (node.getFile().getFileType() != FileTypes.PLAINTEXT)
            return node;
        return null;
    }

    private Node getNode(Path directoryPath){
        return getNodeRecursively(directoryPath, root);
    }

    private Node getNodeRecursively(Path directoryPath, Node branch){
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

        Node node = searchNode(branch, directoryPath.getName(0).toString());
        return getNodeRecursively(dp, node);
    }

    public FileEntity[] getChildFilesEntities(FileEntity file){
        Node parentNode = getNode(file.getDirectoryPath());
        if(parentNode == null) return null;
        int i = 0;
        FileEntity[] fileEntities = new FileEntity[parentNode.size()];
        for (Node childNode : parentNode.nodes) fileEntities[i++] = childNode.getFile();
        return fileEntities;
    }

    public FileEntity[] getFilesEntitiesInDirectory(Path directoryPath){
        Node parentNode = getNode(directoryPath);
        if(parentNode == null) return null;
        int i = 0;
        FileEntity[] fileEntities = new FileEntity[parentNode.size()];
        for (Node childNode : parentNode.nodes) fileEntities[i++] = childNode.getFile();
        return fileEntities;
    }

    public FileEntity getFileEntityByPath(Path path){
        Node node = getNode(path);
        if(node == null) return null;
        return node.getFile();
    }



}
