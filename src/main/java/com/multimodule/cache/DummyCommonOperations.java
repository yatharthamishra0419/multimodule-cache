package com.multimodule.cache;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DummyCommonOperations<V> implements IAppAwareCommonCacheOperations<V> {
    @Override
    public Map<String, ?> multiget(String[] cacheKeys) {
        return null;
    }

    @Override
    public void set(String key, V data, long ttl, TimeUnit unit) {

    }

    @Override
    public void multidelete(String[] keys) {

    }

    @Override
    public V get(String cacheKey) {
        return null;
    }

    @Override
    public void delete(String key) {

    }
}
