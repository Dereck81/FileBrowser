package pe.edu.utp.filebrowser.DSA;

public class HashMap<K, V> {
    private final int __BUCKETLENGTH__ = 15;

    private static class KeyValuePair<K1, V1>{
        private final K1 key;
        private V1 value;

        public KeyValuePair(K1 key, V1 value) {
            this.key = key;
            this.value = value;
        }

        public K1 getKey() {
            return key;
        }

        public V1 getValue(){
            return value;
        }

        public void setValue(V1 value) {
            this.value = value;
        }

    }

    private static class Bucket<K2, V2> {
        private DynamicArray<KeyValuePair<K2, V2>> bucket_kvps = new DynamicArray<>();

        public Bucket(K2 key, V2 value) {
            bucket_kvps.pushBack(new KeyValuePair<>(key, value));
        }

        public Bucket() {}

        public void addKeyValuePair(K2 key, V2 value) {
            bucket_kvps.pushBack(new KeyValuePair<>(key, value));
        }

        public void removeKeyValuePair(int index){
            bucket_kvps.delete(index);
        }

        @SuppressWarnings("unchecked")
        public K2[] getKeys() {
            Object[] keys = new Object[bucket_kvps.size()];
            for (int i = 0; i < bucket_kvps.size(); i++)
                keys[i] = bucket_kvps.at(i).getKey();
            return (K2[]) keys;
        }

        public V2 getValue(K2 key) {
            for(KeyValuePair<K2, V2> pair : bucket_kvps)
                if(pair.getKey().equals(key))
                    return pair.getValue();
            return null;
        }


    }

    @SuppressWarnings("unchecked")
    private Bucket<K, V>[] buckets = new Bucket[__BUCKETLENGTH__];

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
        for(K bucket : buckets[index].getKeys()) {
            if (bucket.equals(key)) {
                buckets[index].removeKeyValuePair(indexTarget);
                break;
            } else indexTarget++;
        }
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

    private long hashCode(K key){
        if(key == null) throw new IllegalArgumentException("key is null!");
        String ky = key.toString();
        int length = ky.length();
        long sum = 0;
        for (char c: ky.toCharArray())
            sum += (int) c * (long) Math.pow(31, length--);
        return sum;
    }

    private int compressFunction(long codeHash){
        return (int) (codeHash%__BUCKETLENGTH__);
    }

}
