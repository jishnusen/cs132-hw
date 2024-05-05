import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IndexedMap<K, V> extends HashMap<K, V> {
    public final List<K> insertionOrder = new ArrayList<>();

    @Override
    public V put(K key, V value) {
        if (!containsKey(key)) {
            insertionOrder.add(key);
        }
        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {
        insertionOrder.remove(key);
        return super.remove(key);
    }

    @Override
    public void clear() {
        insertionOrder.clear();
        super.clear();
    }
}
