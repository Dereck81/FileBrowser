package pe.edu.utp.filebrowser.FileSystem;

import pe.edu.utp.filebrowser.OS.OSUtils;

import java.io.Serializable;
import java.util.Arrays;

public class Path implements Comparable<Path>, Serializable {
    private String path;
    private String root;
    public static String separatorToUseRgx = (OSUtils.isWindows()) ? "\\\\" : "/";
    public static String separatorToUse = (OSUtils.isWindows()) ? "\\" : "/";

    public Path(String path) throws IllegalArgumentException{
        setPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) throws IllegalArgumentException {
        if(!isValid(path)) throw new IllegalArgumentException("Invalid path!");
        if (path.startsWith(separatorToUse)) this.root = separatorToUse;
        else root = null;
        this.path = path;
    }

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

    public Path getParent(){
        int idx = path.lastIndexOf(separatorToUse);
        if(idx == 0) return new Path(root);
        if(idx == -1) return null;
        return new Path(path.substring(0, idx));
    }

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

    public Path resolve(Path path) throws IllegalArgumentException{
        return resolve(path.getPath());
    }

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

    public Path subpath(int beginIndex, int endIndex){
        if (beginIndex < 0) beginIndex = 0;
        if (endIndex < 0) endIndex = 0;
        String auxPath = path.replaceAll("^[\\\\/]+", "");
        String[] arr = Arrays.copyOfRange(auxPath.split(separatorToUseRgx), beginIndex, endIndex);
        return new Path(String.join(separatorToUse, arr));
    }

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

    public String getRoot() {
        return root;
    }

    public static String replaceSeparator(String path) {
        return path.replaceAll("[\\\\|/]+", separatorToUseRgx);
    }

    public static String generateValidPath(String path, boolean includeRoot) {
        if (path == null) return null;
        if (path.isEmpty() || path.isBlank()) return separatorToUse;
        String pth = replaceSeparator(path).replaceAll(
                "^[^a-zA-Z0-9-_. ]+|[^a-zA-Z0-9-_. ]+$",
                "");
        if(includeRoot && !pth.startsWith(separatorToUse)) return separatorToUse+pth;
        return pth;
    }

    public static boolean isValid(String path){
        if(path.matches("^[\\\\/]")) return true;
        return path.matches(
                "^[\\\\/]?[A-Za-z0-9-_.]+(?:[ \\\\/][A-Za-z0-9-_.]+)*[\\\\/]?$");
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if(obj == null || obj.getClass() != getClass())
            return false;

        return this.path.equals(((Path) obj).getPath());
    }

    @Override
    public int compareTo(Path o) {
        return path.compareTo(o.path);
    }
}
