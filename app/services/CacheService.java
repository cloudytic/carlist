package services;

import play.cache.SyncCacheApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class CacheService {
    private SyncCacheApi cache;

    public static CacheService C;

    @Inject
    public CacheService(SyncCacheApi cache) {
        this.cache = cache;

        C = this;
    }


    public void set(String key, Object value) {
        cache.set(key, value);
    }

    public void set(String key, Object value, int expiration) {
        cache.set(key, value, expiration);
    }

    public Optional get(String key) {
        return cache.get(key);
    }

    public void remove(String key) {
        cache.remove(key);
    }
}
