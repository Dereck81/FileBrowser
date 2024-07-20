package pe.edu.utp.filebrowser.OS;

import pe.edu.utp.filebrowser.Enums.OSType;

/**
 * OSUtils
 * Provides utility methods related to the operating system.
 */
public class OSUtils {

    /**
     * Retrieves the name of the current operating system.
     *
     * @return The name of the operating system as reported by system properties.
     */
    public static String getOperatingSystemName(){
        return System.getProperty("os.name");
    }

    /**
     * Retrieves the type of the current operating system.
     *
     * @return The OSType enumeration representing the current operating system.
     */
    public static OSType getOperatingSystem(){
        String osName = getOperatingSystemName().toLowerCase();

        if(osName.contains("windows")) return OSType.WINDOWS;
        else if(osName.contains("linux")) return OSType.LINUX;
        else if(osName.contains("mac")) return OSType.MAC;

        return null;
    }

    /**
     * Checks if the current operating system is Windows.
     *
     * @return true if the current operating system is Windows, false otherwise.
     */
    public static boolean isWindows() {
        return getOperatingSystem() == OSType.WINDOWS;
    }

    /**
     * Checks if the current operating system is macOS.
     *
     * @return true if the current operating system is macOS, false otherwise.
     */
    public static boolean isMac() {
        return getOperatingSystem() == OSType.MAC;
    }

    /**
     * Checks if the current operating system is Linux.
     *
     * @return true if the current operating system is Linux, false otherwise.
     */
    public static boolean isLinux() {
        return getOperatingSystem() == OSType.LINUX;
    }

    /**
     * Retrieves a system property value.
     *
     * @param property The name of the system property to retrieve.
     * @return The value of the specified system property.
     */
    public static String getProperty(String property) {
        return System.getProperty(property);
    }

}
