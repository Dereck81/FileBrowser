package pe.edu.utp.filebrowser.DSA;

@FunctionalInterface
public interface KeyUpdater<K, V> {
    K update(K key, V value);
}