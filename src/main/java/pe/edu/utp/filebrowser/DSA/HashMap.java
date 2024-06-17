package pe.edu.utp.filebrowser.DSA;

import java.math.BigInteger;

public class HashMap<K, V> {
    private final Integer __BUCKETLENGTH__ = 40;
    @SuppressWarnings("unchecked")
    private final Bucket<K, V>[] buckets = new Bucket[__BUCKETLENGTH__];
    private final DynamicArray<K> generalKeys = new DynamicArray<>();

    private static class KeyValuePair<K2, V2>{
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

    private class Bucket<K1, V1> {
        private final DynamicArray<KeyValuePair<K1, V1>> bucket_kvps = new DynamicArray<>();

        @SuppressWarnings("unchecked")
        public Bucket(K1 key, V1 value) {
            bucket_kvps.pushBack(new KeyValuePair<>(key, value));
            generalKeys.pushBack((K) key);
        }

        public Bucket() {}

        @SuppressWarnings("unchecked")
        public void addKeyValuePair(K1 key, V1 value) {
            bucket_kvps.pushBack(new KeyValuePair<>(key, value));
            generalKeys.pushBack((K) key);
        }

        public void removeKeyValuePair(int index){
            bucket_kvps.delete(index);
            // removeGK?

        }

        @SuppressWarnings("unchecked")
        public K1[] getKeys() {
            Object[] keys = new Object[bucket_kvps.size()];
            for (int i = 0; i < bucket_kvps.size(); i++)
                keys[i] = bucket_kvps.at(i).getKey();
            return (K1[]) keys;
        }

        private void removeGK(K1 key){
            int indexTarget = 0;
            for(K key_: generalKeys){
                if(key_.equals(key)){
                    generalKeys.delete(indexTarget);
                    return;
                }else indexTarget++;
            }
        }

        public V1 getValue(K1 key) {
            for(KeyValuePair<K1, V1> pair : bucket_kvps)
                if(pair.getKey().equals(key))
                    return pair.getValue();
            return null;
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

    public void remove(K key){
        int index = indexGenerator(key);
        int indexTarget = 0;
        for(K key_ : buckets[index].getKeys()) {
            if (key_.equals(key)) {
                buckets[index].removeKeyValuePair(indexTarget);
                removeDA(key);
                break;
            } else indexTarget++;
        }
    }

    private void removeDA(K key){
        int index = 0;
        for (K k: generalKeys)
            if(k.equals(key)){
                generalKeys.delete(index);
                return;
            }else index++;
    }

    public boolean contains(K key){
        int index = indexGenerator(key);
        if(buckets[index] == null) return false;
        for(K bucket : buckets[index].getKeys())
            if(bucket.equals(key)) return true;
        return false;
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

    public BigInteger djb2(K key) {
        //int hash = 5381;
        BigInteger hash = BigInteger.valueOf(5381);
        String key_ = key.toString();
        for (int i = 0; i < key_.length(); i++) {
            hash =  BigInteger.valueOf((long) hash.intValue() << 5 )
                    .add(hash)
                    .add(BigInteger.valueOf(key_.charAt(i)));
            //hash = ((hash << 5) + hash) + key_.charAt(i);
        }

        return hash;
    }

    private int compressFunction(BigInteger codeHash){
        return codeHash.mod(BigInteger.valueOf((long) __BUCKETLENGTH__)).intValue();
    }

}
