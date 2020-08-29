package com.multimodule.cache.jvmcache;

import com.multimodule.cache.IAppAwareCommonCacheOperations;
import org.apache.commons.collections.map.LRUMap;

import java.util.Map;

public class JvmCacheOperationsContainer {

    private int lruMapSize;
    private  Map<String, Object> cacheMap;
    private IAppAwareCommonCacheOperations iAppAwareCommonCacheOperations;

    public JvmCacheOperationsContainer(int lruMapSize) {
        this.lruMapSize=lruMapSize;
        cacheMap =new LRUMap(lruMapSize);
    }

    public int getLruMapSize() {
        return lruMapSize;
    }

    public void setLruMapSize(int lruMapSize) {
        this.lruMapSize = lruMapSize;
    }

    public Map<String, Object> getCacheMap() {
        return cacheMap;
    }

    public void setCacheMap(Map<String, Object> cacheMap) {
        this.cacheMap = cacheMap;
    }

    public IAppAwareCommonCacheOperations getiAppAwareCommonCacheOperations() {
        return iAppAwareCommonCacheOperations;
    }

    public void setiAppAwareCommonCacheOperations(IAppAwareCommonCacheOperations iAppAwareCommonCacheOperations) {
        this.iAppAwareCommonCacheOperations = iAppAwareCommonCacheOperations;
    }
}


