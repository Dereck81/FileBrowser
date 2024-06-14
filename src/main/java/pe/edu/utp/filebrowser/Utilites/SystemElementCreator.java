package pe.edu.utp.filebrowser.Utilites;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SystemElementCreator {

    public static boolean createDirectories(Path rootDirectory, Path directoryPath) throws IOException {
        if(rootDirectory == null || directoryPath == null) return false;
        return createDirectoriesRecursively(rootDirectory, directoryPath, 0);
    }

    private static boolean createDirectoriesRecursively(Path rootDirectory, Path directoryPath, int index) throws IOException {
        if(directoryPath.getNameCount()-1 == index) {
            Files.createDirectory(rootDirectory.resolve(directoryPath.getName(index)));
            return true;
        }
        Path newRootDirectory = rootDirectory.resolve(directoryPath.getName(index));
        Files.createDirectory(newRootDirectory);
        return createDirectoriesRecursively(newRootDirectory, directoryPath, index+1);
    }

}
