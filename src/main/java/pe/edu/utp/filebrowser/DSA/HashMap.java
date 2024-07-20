package pe.edu.utp.filebrowser.DSA;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.function.Predicate;

/**
 * HashMap
 * An implementation of a HashMap data structure with key-value pairs, supporting basic operations
 * like put, get, remove, and update based on custom hashing and collision resolution strategies.
 *
 * @param <K> the type of keys stored in the map
 * @param <V> the type of values associated with the keys
 */
public class HashMap<K, V> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1633468994188749630L;
    private final Integer DEFAULT_CAPACITY = 40;
    private final DynamicArray<K> generalKeys = new DynamicArray<>();
    private int size = 0;

    @SuppressWarnings("unchecked")
    private Bucket<K, V>[] buckets = new Bucket[DEFAULT_CAPACITY];

    // region [INNER CLASSES]

    /**
     * KeyValuePair
     * Represents a key-value pair used within the HashMap.
     *
     * @param <K2> the type of the key
     * @param <V2> the type of the value
     */
    private class KeyValuePair<K2, V2> implements Serializable{
        private final K2 key;
        private V2 value;

        public KeyValuePair(K2 key, V2 value) {
            this.key = key;
            this.value = value;
        }

        public K2 getKey() {
            return key;
        }

        public V2 getValue(){
            return value;
        }

        public void setValue(V2 value) {
            this.value = value;
        }

    }

    /**
     * Bucket
     * Represents a bucket that holds multiple key-value pairs.
     *
     * @param <K1> the type of the key in the bucket
     * @param <V1> the type of the value in the bucket
     */
    private class Bucket<K1, V1> implements Serializable {
        private final DynamicArray<KeyValuePair<K1, V1>> bucket_kvps = new DynamicArray<>();

        @SuppressWarnings("unchecked")
        public Bucket(K1 key, V1 value) {
            bucket_kvps.pushBack(new KeyValuePair<>(key, value));
            generalKeys.pushBack((K) key);
            size++;
        }

        public Bucket() {}

        @SuppressWarnings("unchecked")
        public void addKeyValuePair(K1 key, V1 value) {
            bucket_kvps.pushBack(new KeyValuePair<>(key, value));
            generalKeys.pushBack((K) key);
            size++;
        }

        @SuppressWarnings("unchecked")
        public void removeKeyValuePair(K1 key){
            int idx = find(key);

            if (idx == -1) return;

            bucket_kvps.delete(idx);
            generalKeys.delete(generalKeys.find((K) key));
            size--;
        }

        @SuppressWarnings("unchecked")
        public K1[] getKeys() {
            Object[] keys = new Object[bucket_kvps.size()];

            for (int i = 0; i < bucket_kvps.size(); i++)
                keys[i] = bucket_kvps.at(i).getKey();

            return (K1[]) keys;
        }

        private int find(K1 key){
            int i = 0;

            for(KeyValuePair<K1, V1> pair : bucket_kvps)
                if(pair.getKey().equals(key))
                    return i;
                else i++;

            return -1;
        }

        public V1 getValue(K1 key) {
            int idx = find(key);

            if(idx == -1) return null;

            return bucket_kvps.at(idx).getValue();
        }


    }

    // endregion

    /**
     * Constructs a HashMap with an initial key-value pair.
     *
     * @param key   the initial key
     * @param value the initial value associated with the key
     */
    public HashMap(K key, V value) {
        buckets[indexGenerator(key)] = new Bucket<>(key, value);
    }

    /**
     * Default constructor for HashMap.
     */
    public HashMap(){}

    // region [BASIC OPERATIONS]

    /**
     * Adds a key-value pair to the HashMap.
     *
     * @param key   the key to add
     * @param value the value associated with the key
     */
    public void put(K key, V value) {
        int index = indexGenerator(key);

        if(buckets[index] == null)
            buckets[index] = new Bucket<>(key, value);
        else
            if(!contains(key))
                buckets[index].addKeyValuePair(key, value);
    }

    /**
     * Retrieves the value associated with a key from the HashMap.
     *
     * @param key the key to retrieve the value for
     * @return the value associated with the key, or null if the key is not found
     */
    public V get(K key) {
        int index = indexGenerator(key);

        if(buckets[index] == null) return null;

        return buckets[index].getValue(key);
    }

    /**
     * Retrieves all keys stored in the HashMap.
     *
     * @return an array of keys in the HashMap
     */
    @SuppressWarnings("unchecked")
    public K[] getKeys() {
        Object[] keys = new Object[generalKeys.size()];

        for (int i = 0; i < generalKeys.size(); i++)
            keys[i] =  generalKeys.at(i);

        return (K[]) keys;
    }

    /**
     * Retrieves keys from the HashMap that satisfy a given condition.
     *
     * @param condition the condition predicate to filter keys
     * @return an array of keys that satisfy the condition
     */
    public K[] getKeys(Predicate<K> condition){
        DynamicArray<K> arr = new DynamicArray<>();

        for (int i = 0; i < generalKeys.size(); i++) {
            K key = generalKeys.at(i);
            if (condition.test(key))
                arr.pushBack(key);
        }

        return arr.toArray();
    }

    /**
     * Removes a key-value pair from the HashMap.
     *
     * @param key the key to remove
     */
    public void remove(K key){
        int index = indexGenerator(key);

        if(buckets[index] == null) return;

        buckets[index].removeKeyValuePair(key);
    }

    /**
     * Checks if the HashMap contains a specified key.
     *
     * @param key the key to check for existence in the HashMap
     * @return true if the key exists, false otherwise
     */
    public boolean contains(K key){
        int index = indexGenerator(key);

        if(buckets[index] == null) return false;

        for(K key_ : buckets[index].getKeys())
            if(key_.equals(key)) return true;

        return false;
    }

    /**
     * Retrieves the current size of the HashMap.
     *
     * @return the number of key-value pairs in the HashMap
     */
    public int getSize(){
        return size;
    }

    // endregion

    // region [UTILITY METHODS]

    /**
     * Generates an index for a key using a custom hashing function.
     *
     * @param key the key to generate an index for
     * @return the index computed using the hashing function
     */
    private int indexGenerator(K key){
        return compressFunction(hashCode(key));
    }

    /**
     * Computes a custom hash code for a key using a polynomial rolling hash function.
     *
     * @param key the key to compute the hash code for
     * @return the computed hash code as a BigInteger
     */
    private BigInteger hashCode(K key){
        if(key == null) throw new IllegalArgumentException("Key is null!");

        String ky = key.toString();
        BigInteger sum = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(31);
        int length = ky.length();

        for (char c: ky.toCharArray())
            sum = sum.add(BigInteger.valueOf((int) c).multiply(base.pow(length--)));

        return sum;
    }

    /**
     * Computes a hash code using the DJB2 algorithm for a key.
     *
     * @param key the key to compute the hash code for
     * @return the computed hash code as a BigInteger
     */
    private BigInteger djb2(K key) {
        //int hash = 5381;
        BigInteger hash = BigInteger.valueOf(5381);
        String key_ = key.toString();

        for (int i = 0; i < key_.length(); i++)
            hash =  BigInteger
                    .valueOf((long) hash.intValue() << 5 )
                    .add(hash)
                    .add(BigInteger.valueOf(key_.charAt(i)));
            //hash = ((hash << 5) + hash) + key_.charAt(i);

        return hash;
    }

    /**
     * Compresses a hash code to fit within the current capacity of the HashMap.
     *
     * @param codeHash the hash code to compress
     * @return the compressed index within the bounds of the HashMap's capacity
     */
    private int compressFunction(BigInteger codeHash){
        return codeHash.mod(BigInteger.valueOf((long) DEFAULT_CAPACITY)).intValue();
    }

    // endregion

    // region [ADVANCED OPERATIONS]

    /**
     * Updates the keys in the HashMap based on a condition using a custom update function.
     *
     * @param condition the condition predicate to select keys for updating
     * @param keyUpdate the function to update keys
     */
    public void update(Predicate<K> condition, KeyUpdater<K, V> keyUpdate){
        K[] keys = getKeys(condition);

        for (int i = 0; i < keys.length; i++) {
            K oldKey = keys[i];
            V value = get(oldKey);
            K newKey = keyUpdate.update(oldKey, value);

            remove(oldKey);
            put(newKey, value);
        }
    }

    /**
     * Removes all keys from the HashMap that satisfy a given condition.
     *
     * @param condition the condition predicate to select keys for removal
     */
    public void remove(Predicate<K> condition){
        K[] keys = getKeys(condition);

        for (int i = 0; i < keys.length; i++)
            remove(keys[i]);
    }

    /**
     * Clears the HashMap, removing all key-value pairs.
     */
    @SuppressWarnings("unchecked")
    public void clear(){
        buckets = new Bucket[DEFAULT_CAPACITY];

        generalKeys.clear();
        size = generalKeys.size();
    }

    // endregion

}
