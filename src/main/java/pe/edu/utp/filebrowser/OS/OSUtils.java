package pe.edu.utp.filebrowser.OS;

import pe.edu.utp.filebrowser.Enums.OSType;

public class OSUtils {

    public static String getOperatingSystemName(){
        return System.getProperty("os.name");
    }

    public static OSType getOperatingSystem(){
        String osName = getOperatingSystemName().toLowerCase();
        if(osName.contains("windows")) return OSType.WINDOWS;
        else if(osName.contains("linux")) return OSType.LINUX;
        else if(osName.contains("mac")) return OSType.MAC;
        return null;
    }

    public static boolean isWindows() {
        return getOperatingSystem() == OSType.WINDOWS;
    }

    public static boolean isMac() {
        return getOperatingSystem() == OSType.MAC;
    }

    public static boolean isLinux() {
        return getOperatingSystem() == OSType.LINUX;
    }

    public static String getProperty(String property) {
        return System.getProperty(property);
    }

}
