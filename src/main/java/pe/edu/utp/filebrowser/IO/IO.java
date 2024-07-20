package pe.edu.utp.filebrowser.IO;

import pe.edu.utp.filebrowser.FileBrowser;
import pe.edu.utp.filebrowser.Utilites.WindowsGlobalExceptionHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class IO {

    private static final String _FILELOG_ =
            Objects.requireNonNull(FileBrowser.class.getResource("logRecentFiles.log"))
                    .getFile()
                    .replaceAll("%20", " ").substring(1);

    private static final int _CANTRECENTFILES_ = 5;

    /**
     * Reads recent file records from the log file.
     * @return a list of strings representing the names of recent files
     */
    public static ArrayList<String> readRecordRecentFiles(){
        ArrayList<String> data;

        try(BufferedReader fi = new BufferedReader(new FileReader(_FILELOG_))){
            data = new ArrayList<>();
            String line;

            while((line = fi.readLine()) != null) data.add(line);

        }catch (IOException e){
            return new ArrayList<>();
        }

        return data;
    }

    /**
     * Writes a new recent file record to the log file.
     * @param path path of the recent file to log
     */
    public static void writeRecordRecentFiles(String path){
        File filelog = new File(_FILELOG_);
        ArrayList<String> content = IO.readRecordRecentFiles();

        if (content.size() > _CANTRECENTFILES_)
            content.removeLast();

        try(FileWriter fileW = new FileWriter(filelog)){
            fileW.write(path+"\n");
            content.removeIf(path_i -> path_i.equals(path));
            fileW.write(String.join("\n", content.toArray(new String[0])));

        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Deletes a recent file record from the log file.
     * @param path path of the recent file to delete from the log
     */
    public static void deleteRecentFilesRecord(String path){
        File filelog = new File(_FILELOG_);
        ArrayList<String> content = IO.readRecordRecentFiles();

        content.removeIf(path_i -> path_i.equals(path));

        try(FileWriter fileW = new FileWriter(filelog)){
            fileW.write(String.join("\n", content.toArray(new String[0])));
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Saves content to a file at the specified path.
     *
     * @param content   The content to write to the file.
     * @param filePath  The path of the file where the content will be saved.
     * @param showAlert Indicates whether to show an alert message upon completion.
     * @throws Exception If an error occurs during file writing.
     */
    public static void saveData(String content, String filePath, boolean showAlert) throws Exception{
        try(FileWriter fileW = new FileWriter(filePath)){
            fileW.write(content);
        }

        if(!showAlert) return;

        WindowsGlobalExceptionHandler.alertInformation(
                "Information",
                "the file was exported successfully");
    }

}
