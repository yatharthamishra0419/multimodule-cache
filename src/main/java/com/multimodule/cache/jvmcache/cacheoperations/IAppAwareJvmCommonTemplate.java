package com.multimodule.cache.jvmcache.cacheoperations;

import com.multimodule.cache.IAppAwareCommonCacheOperations;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class IAppAwareJvmCommonTemplate implements IAppAwareCommonCacheOperations {

    private Map<String,Object> lruMap;

    public IAppAwareJvmCommonTemplate(Map<String,Object> lruMap){
        this.lruMap =lruMap;
    }

    @Override
    public Map<String, ?> multiget(String[] cacheKeys) {
        Map<String, Object> data = new LinkedHashMap<>(cacheKeys.length);
        for (int i = 0; i < cacheKeys.length; i++) {
            data.put(cacheKeys[i], lruMap.get(cacheKeys[i]));
        }
        return data;
    }

    @Override
    public void set(String key, Object data, long ttl, TimeUnit unit) {
        lruMap.put(key, data);
    }

    @Override
    public void multidelete(String[] keys)  {
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                lruMap.remove(keys[i]);
            }
        }
    }

    @Override
    public Object get(String cacheKey) {
        String[] key=new String[1];
        key[0]=cacheKey;
        Map<String, ?> multiget = multiget(key);
        return multiget.get(cacheKey);
    }

    @Override
    public void delete(String cacheKey) {
        String[] key=new String[1];
        key[0]=cacheKey;
        multidelete(key);
    }

}
