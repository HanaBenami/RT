package il.co.rtcohen.rt.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vaadin.data.ValueProvider;
import il.co.rtcohen.rt.app.views.CallsView;
import il.co.rtcohen.rt.dal.dao.interfaces.Cloneable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CacheManager<T extends Cloneable<T>> {
    private static final Logger logger = LoggerFactory.getLogger(CallsView.class);

    LoadingCache<Integer, T> cache;
    ValueProvider<T, Integer> hashKeyProvider;

    public CacheManager(ValueProvider<T, Integer> hashKeyProvider) {
        this.hashKeyProvider = hashKeyProvider;
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(3000)
                .build(
                        new CacheLoader<Integer, T>() {
                            @Override
                            public T load(Integer key) {
                                return null;
                            }
                        }
                );
    }

    public void addToCache(T t) {
        this.cache.put(this.hashKeyProvider.apply(t), t);
    }

    public void addToCache(List<T> list) {
        for (T t : list) {
            addToCache(t);
        }
    }

    public T getFromCache(Integer hashKey) {
        T fromCache = this.cache.getIfPresent(hashKey);
        return (null == fromCache ? null : fromCache.cloneObject());
    }

    public void deleteFromCache(T t) {
        this.cache.invalidate(this.hashKeyProvider.apply(t));
    }
}
