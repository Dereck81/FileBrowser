package pe.edu.utp.filebrowser.IO;

import pe.edu.utp.filebrowser.DSA.FileNode;
import pe.edu.utp.filebrowser.FileSystem.*;

import java.nio.file.Files;
import java.nio.file.Path;

public class SystemElementCreator {

    /**
     * Recursively traverses a tree of file and directory nodes and creates them on the specified destination path.
     *
     * @param root The root node of the file and directory tree.
     * @param dest The destination path where the file system structure will be created.
     * @throws Exception If an error occurs during file or directory creation.
     */
    public static void createFileSystemStructure(FileNode root, String dest) throws Exception {
        Path target = Path.of(dest, root.getFile().getName());

        if(root.getFile() instanceof VirtualDiskDriver || root.getFile() instanceof Folder)
            try {Files.createDirectory(target);} catch (Exception _){}
        else if(root.getFile() instanceof PlainText)
            IO.saveData(((PlainText) root.getFile()).getContent(),
                    String.valueOf(target), false);

        System.out.println("creating: "+target);

        for (FileNode child : root.getChildren())
            createFileSystemStructure(child, target.toString());

    }

}
