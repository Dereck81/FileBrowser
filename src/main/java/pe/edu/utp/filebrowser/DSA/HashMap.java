package pe.edu.utp.filebrowser.DSA;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.function.Predicate;

public class HashMap<K, V> implements Serializable {
    private final Integer DEFAULT_CAPACITY = 40;
    private final DynamicArray<K> generalKeys = new DynamicArray<>();
    private int size = 0;

    @SuppressWarnings("unchecked")
    private final Bucket<K, V>[] buckets = new Bucket[DEFAULT_CAPACITY];

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


    public HashMap(K key, V value) {
        buckets[indexGenerator(key)] = new Bucket<>(key, value);
    }

    public HashMap(){}

    public void put(K key, V value) {
        int index = indexGenerator(key);
        if(buckets[index] == null)
            buckets[index] = new Bucket<>(key, value);
        else
            if(!contains(key))
                buckets[index].addKeyValuePair(key, value);
    }

    public V get(K key) {
        int index = indexGenerator(key);
        if(buckets[index] == null) return null;
        return buckets[index].getValue(key);
    }

    @SuppressWarnings("unchecked")
    public K[] getKeys() {
        Object[] keys = new Object[generalKeys.size()];
        for (int i = 0; i < generalKeys.size(); i++)
            keys[i] =  generalKeys.at(i);
        return (K[]) keys;
    }

    @SuppressWarnings("unchecked")
    public K[] getKeys(Predicate<K> condition){
        DynamicArray<K> arr = new DynamicArray<>();
        for (int i = 0; i < generalKeys.size(); i++) {
            K key = generalKeys.at(i);
            if (condition.test(key))
                arr.pushBack(key);
        }
        return arr.toArray();
    }

    public void remove(K key){
        int index = indexGenerator(key);
        if(buckets[index] == null) return;
        buckets[index].removeKeyValuePair(key);
    }

    public boolean contains(K key){
        int index = indexGenerator(key);
        if(buckets[index] == null) return false;
        for(K key_ : buckets[index].getKeys())
            if(key_.equals(key)) return true;
        return false;
    }

    public int getSize(){
        return size;
    }

    private int indexGenerator(K key){
        return compressFunction(hashCode(key));
    }

    private BigInteger hashCode(K key){
        if(key == null) throw new IllegalArgumentException("Key is null!");
        String ky = key.toString();
        int length = ky.length();
        BigInteger sum = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(31);
        for (char c: ky.toCharArray())
            sum = sum.add(BigInteger.valueOf((int) c).multiply(base.pow(length--)));
        return sum;
    }

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

    private int compressFunction(BigInteger codeHash){
        return codeHash.mod(BigInteger.valueOf((long) DEFAULT_CAPACITY)).intValue();
    }

    private void update(K key, V newValue){
        remove(key);
        put(key, newValue);
    }

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

    public void remove(Predicate<K> condition){
        K[] keys = getKeys(condition);
        for (int i = 0; i < keys.length; i++)
            remove(keys[i]);
    }

}
