package pe.edu.utp.filebrowser.IO;

import javafx.scene.control.TreeItem;
import pe.edu.utp.filebrowser.DSA.HashMap;
import pe.edu.utp.filebrowser.DSA.Tree;
import pe.edu.utp.filebrowser.FileSystem.FileEntity;
import pe.edu.utp.filebrowser.FileSystem.Path;

import java.io.*;
import java.util.function.Predicate;

public class ObjectSerializationUtil {

    public static boolean serialize(HashMap<Path, TreeItem<FileEntity>> fat, Tree tree, String pathFile){
        HashMap<Path, FileEntity> newFat = convertPathTreeItemMap(fat);
        try (FileOutputStream fileOut = new FileOutputStream(pathFile)){
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(newFat);
            out.writeObject(tree);
            out.close();
            fileOut.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static Object[] deserialize(String pathFile){
        // return {HashMap, Tree, rootItem}
        Object[] objects = new Object[2];
        try (FileInputStream fileIn = new FileInputStream(pathFile)){
            ObjectInputStream in = new ObjectInputStream(fileIn);
            objects[0] = in.readObject();
            objects[1] = in.readObject();
            in.close();
            fileIn.close();

            return objects;
        }catch (Exception e){
            return objects;
        }
    }

    private static HashMap<Path, TreeItem<FileEntity>> convertPathFileEntityMap(HashMap<Path, FileEntity> fat){
        HashMap<Path, TreeItem<FileEntity>> newFat = new HashMap<>();
        Object[] keys = fat.getKeys();
        for (Object key : keys) {
            Path path = (Path) key;
            //newFat.put(path, fat.get(path));
        }

        return newFat;
    }

    private static HashMap<Path, FileEntity> convertPathTreeItemMap(HashMap<Path, TreeItem<FileEntity>> fat){
        HashMap<Path, FileEntity> newFat = new HashMap<>();
        // Here it generates a casting error, it doesn't make sense
        // and this was the only way to not throw an exception.
        // The array that fat.getKeys() should return should be of type Path,
        // when in reality it is of type Object. ¿¿¿???
        // and this also happens directly in the HashMap class
        // Also, when trying to cast from supposedly Object[] to Path[],
        // it indicates that it is redundant. ????
        //
        // Error line: Path[] keys = (Path[]) fat.getKeys();
        // GetKeys() call failed
        // Only solution for the moment
        Object[] keys = fat.getKeys();
        for (Object key : keys) {
            Path path = (Path) key;
            newFat.put(path, fat.get(path).getValue());
        }
        return newFat;
    }

}
