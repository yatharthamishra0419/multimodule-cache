package com.multimodule.cache;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface IAppAwareCommonCacheOperations<V> {

    Map<String,?> multiget(String[] cacheKeys) ;

    void set(String key, V data, long ttl, TimeUnit unit) ;


    void multidelete(String[] keys) ;

    V get(String cacheKey);

    void delete(String key);
}
