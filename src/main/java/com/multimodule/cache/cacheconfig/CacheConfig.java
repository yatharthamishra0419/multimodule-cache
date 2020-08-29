package com.multimodule.cache.cacheconfig;


import com.multimodule.cache.CacheConstants;
import com.multimodule.cache.utils.GeneralUtils;

import java.util.concurrent.TimeUnit;

public class CacheConfig {

    public CacheConfig() {
    }

    public CacheConfig(Long ttl, String prefix,String setName,TimeUnit timeUnit) {
        if(ttl!=null)
        this.ttl = ttl;
        if(prefix!=null)
        this.prefix = prefix;
        if(setName!=null)
            this.setName=setName;
        if(!GeneralUtils.isNullOrEmpty(timeUnit))
            this.timeUnit=timeUnit;
    }

    long ttl= CacheConstants.CACHE_DEFAULT_TTL_SECONDS;
    String prefix="";
    String setName=CacheConstants.DEFAULT_SET_NAME;
    TimeUnit timeUnit=TimeUnit.SECONDS;

    public long getTtl() {
        return ttl;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSetName() {
        return setName;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
