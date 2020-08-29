package com.multimodule.cache.aerospike.templates;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface IAeroSpikeCommonTemplate<V> {
    public List<V> multiGet(String cacheName, Collection<String> keys);
    public void set(String cacheName, String key, V value, long timeout, TimeUnit unit);
    public void delete(String cacheName, Collection<String> keys);
}
