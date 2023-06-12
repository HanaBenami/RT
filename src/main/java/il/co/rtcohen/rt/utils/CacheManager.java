package il.co.rtcohen.rt.utils;

import com.vaadin.data.ValueProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CacheManager<T> {
    ValueProvider<T, Integer> hashKeyProvider;
    HashMap<Integer, T> cache;

    public CacheManager(ValueProvider<T, Integer> hashKeyProvider) {
        this.hashKeyProvider = hashKeyProvider;
        this.cache = new HashMap<>();
    }

    // Add an object to the cache.
    // If a value with the same hash key already exists, it will be gone.
    public void addToCache(T t) {
        this.cache.put(this.hashKeyProvider.apply(t), t);
    }

    // Get an object with this hash key from the cache, if exists.
    public T getFromCache(Integer hashKey) {
        return this.cache.getOrDefault(hashKey, null);
    }

    // Get the copy of this object (i.e. same hash key) from the cache, if exists.
    public T getFromCache(T t) {
        return this.cache.getOrDefault(this.hashKeyProvider.apply(t), null);
    }

    // Remove this object from the cache, if exists.
    public void deleteFromCache(T t) {
        this.cache.remove(this.hashKeyProvider.apply(t));
    }

    // If a value with the same hash key already exists in the cache, it will be returned.
    // If not, the given object will be added to the cache and returned.
    public T syncWithCache(T t) {
        T fromCache = getFromCache(t);
        if (null == fromCache) {
            addToCache(t);
            return t;
        } else {
            return fromCache;
        }
    }

    // For each object in the list:
    // If a value with the same hash key already exists in the cache, it will be added to the list, instead of the original object.
    // If not, the given object will be added to the cache and remain in the list.
    public List<T> syncWithCache(List<T> list) {
        List<T> copy = new ArrayList<>(list);
        for (T t : copy) {
            T fromCache = getFromCache(t);
            if (null == fromCache) {
                addToCache(t);
            } else {
                list.add(list.indexOf(t), fromCache);
                list.remove(t);
            }
        }
        return list;
    }
}
