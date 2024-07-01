package pe.edu.utp.filebrowser.FileSystem;

import pe.edu.utp.filebrowser.OS.OSUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a file system path.
 */
public class Path implements Comparable<Path>, Serializable {
    private String path;
    private String root;
    public static String separatorToUseRgx = (OSUtils.isWindows()) ? "\\\\" : "/";
    public static String separatorToUse = (OSUtils.isWindows()) ? "\\" : "/";


    /**
     * Constructs a Path object with the specified path string.
     *
     * @param path The path string.
     * @throws IllegalArgumentException If the path is invalid.
     */
    public Path(String path) throws IllegalArgumentException{
        setPath(path);
    }

    /**
     * Retrieves the path string of this Path object.
     *
     * @return The path string.
     */
    public String getPath() {
        return path;
    }

    /**
     * Retrieves the root part of the path.
     *
     * @return The root part of the path.
     */
    public String getRoot() {
        return root;
    }

    /**
     * Sets the path string of this Path object.
     *
     * @param path The new path string to set.
     * @throws IllegalArgumentException If the path is invalid.
     */
    public void setPath(String path) throws IllegalArgumentException {
        if(!isValid(path)) throw new IllegalArgumentException("Invalid path!");
        if (path.startsWith(separatorToUse)) this.root = separatorToUse;
        else root = null;
        this.path = path;
    }

    /**
     * Retrieves a specific name segment from the path based on index.
     *
     * @param idx The index of the name segment.
     * @return A Path object representing the name segment.
     */
    public Path getName(int idx){
        String auxPath = path;
        if(auxPath.startsWith(separatorToUse))
            auxPath = auxPath.substring(1);
        if(auxPath.endsWith(separatorToUse))
            auxPath = auxPath.substring(0, auxPath.length()-1);
        return new Path(auxPath.split(separatorToUseRgx)[idx]);
    }

    /**
     * Calculates the depth of a path. The depth is determined by the amount of / excluding the starting and the
     * last ones.
     * @return depth of a path.
     */
    public int getNameCount(){
        String auxPath = path;
        if(path.equals(separatorToUse)) return 0;

        if(auxPath.startsWith(separatorToUse))
            auxPath = auxPath.substring(1);

        if(auxPath.endsWith(separatorToUse))
            auxPath = auxPath.substring(0, auxPath.length()-1);

        return auxPath.split(separatorToUseRgx).length;
    }

    /**
     * Retrieves the parent path of the current path.
     *
     * @return A Path object representing the parent path.
     */
    public Path getParent(){
        int idx = path.lastIndexOf(separatorToUse);
        if(idx == 0) return new Path(root);
        if(idx == -1) return null;
        return new Path(path.substring(0, idx));
    }

    /**
     * Retrieves the file or directory name from the path.
     *
     * @return The file or directory name.
     */
    public String getFileName(){
        String auxPath = path;
        String[] splitPath;
        if(auxPath.startsWith(separatorToUse))
            auxPath = auxPath.substring(1);
        if(auxPath.endsWith(separatorToUse))
            auxPath = auxPath.substring(0, auxPath.length()-1);
        splitPath = auxPath.split(separatorToUseRgx);
        return splitPath[splitPath.length-1];
    }

    /**
     * Resolves a given path against this path.
     *
     * @param path The path to resolve against this path.
     * @return A new Path object representing the resolved path.
     * @throws IllegalArgumentException If the path is invalid.
     */
    public Path resolve(Path path) throws IllegalArgumentException{
        return resolve(path.getPath());
    }

    /**
     * Resolves a given string path against this path.
     *
     * @param path The string path to resolve against this path.
     * @return A new Path object representing the resolved path.
     * @throws IllegalArgumentException If the path is invalid.
     */
    public Path resolve(String path) throws IllegalArgumentException{
        if(path == null) return this;
        if(!isValid(path)) throw new IllegalArgumentException("Invalid path!");
        String newPath =
                this.path.replaceAll("[\\\\/]+$", "")
                        + separatorToUse
                        + path.replaceAll("^[\\\\/]+|[\\\\/]+$", "");
        if(!isValid(newPath)) throw new IllegalArgumentException("Invalid path!");
        return new Path(newPath);
    }

    /**
     * Retrieves a subpath of this path.
     *
     * @param beginIndex The beginning index, inclusive.
     * @param endIndex   The ending index, exclusive.
     * @return A new Path object representing the subpath.
     */
    public Path subpath(int beginIndex, int endIndex){
        if (beginIndex < 0) beginIndex = 0;
        if (endIndex < 0) endIndex = 0;
        String auxPath = path.replaceAll("^[\\\\/]+", "");
        String[] arr = Arrays.copyOfRange(auxPath.split(separatorToUseRgx), beginIndex, endIndex);
        return new Path(String.join(separatorToUse, arr));
    }

    /**
     * Sets the path to a list of given paths.
     *
     * @param paths The paths to set.
     * @throws IllegalArgumentException If any path is invalid.
     */
    public void of(String... paths) throws IllegalArgumentException {
        String auxPath = path;
        String pth;
        for (String path : paths) {
            if(path == null){
                this.path = auxPath;
                throw new IllegalArgumentException("Invalid path! -> null");
            }

            pth = generateValidPath(path, false);

            if(!isValid(pth)){
                this.path = auxPath;
                throw new IllegalArgumentException("Invalid path!");
            }

            if(pth.startsWith(separatorToUse))
                this.path = this.path.replaceAll("[\\\\/]+$", "")+ pth;
            else
                this.path = this.path.replaceAll("[\\\\/]+$", "")
                        + separatorToUse + pth;
        }
        if(!isValid(this.path)){
            this.path = auxPath;
            throw new IllegalArgumentException("Invalid path!");
        }
    }

    /**
     * Replaces the path separator in a given path string with the appropriate OS-specific separator.
     *
     * @param path The path string to modify.
     * @return The modified path string with correct separators.
     */
    public static String replaceSeparator(String path) {
        return path.replaceAll("[\\\\|/]+", separatorToUseRgx);
    }

    /**
     * Generates a valid path string from a given path, ensuring it adheres to file system naming conventions.
     *
     * @param path         The path string to validate.
     * @param includeRoot  Whether to include the root separator in the path.
     * @return A valid path string.
     */
    public static String generateValidPath(String path, boolean includeRoot) {
        if (path == null) return null;
        if (path.isEmpty() || path.isBlank()) return separatorToUse;
        String pth = replaceSeparator(path).replaceAll(
                "^[^a-zA-Z0-9-_. ]+|[^a-zA-Z0-9-_. ]+$",
                "");
        if(includeRoot && !pth.startsWith(separatorToUse)) return separatorToUse+pth;
        return pth;
    }

    /**
     * Checks if a given path string is valid.
     *
     * @param path The path string to validate.
     * @return True if the path is valid, false otherwise.
     */
    public static boolean isValid(String path){
        if(path.matches("^[\\\\/]")) return true;
        return path.matches(
                "^[\\\\/]?[A-Za-z0-9-_.]+(?:[ \\\\/][A-Za-z0-9-_.]+)*[\\\\/]?$");
    }

    // region [OVERRIDDEN METHODS]

    /**
     * Retrieves the string representation of this Path object.
     *
     * @return The path string.
     */
    @Override
    public String toString() {
        return path;
    }

    /**
     * Checks if this Path object is equal to another object.
     *
     * @param obj The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if(obj == null || obj.getClass() != getClass())
            return false;

        return this.path.equals(((Path) obj).getPath());
    }

    /**
     * Compares this Path object with another Path object for ordering.
     *
     * @param o The Path object to compare.
     * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Path o) {
        return path.compareTo(o.path);
    }

    // endregion
}
