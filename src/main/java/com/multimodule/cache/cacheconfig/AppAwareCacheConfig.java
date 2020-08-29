package com.multimodule.cache.cacheconfig;

import com.multimodule.cache.multimodule.ModuleAwarePropertiesUtils;
import com.multimodule.cache.utils.GeneralUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Configuration
public class AppAwareCacheConfig {

    private Map<String, CacheConfig> cacheConfigMap = new ConcurrentHashMap<>();

    private final String CACHE_TTL_PREFIX = "ttlinseconds";

    private final String CACHE_KEY_PREFIX = "prefix";

    private final String CACHE_SET_NAME = "setname";

    private final String CACHE_TIME_UNIT="timeunit";

    private final long CACHE_TTL_DEFAULT_VALUE = 2000;

    @Autowired
    private Environment environment;

    private static final String CACHE_NAME_PREFIX = "cache.";


    @PostConstruct
    public void populateMap() {
        final Map<String, Map<String, String>> moduleWiseCacheProperties =
                ModuleAwarePropertiesUtils.readModuleWiseSubProperties(environment, CACHE_NAME_PREFIX);
        for (Map.Entry<String, Map<String, String>> cacheMapEntry : moduleWiseCacheProperties
                .entrySet()) {
            String moduleName = cacheMapEntry.getKey();
            Map<String, String> cacheValue = cacheMapEntry.getValue();
            String cacheNames = cacheValue.get("names");
            if(GeneralUtils.isNullOrEmpty(cacheNames))
                continue;
            String[] cacheNamesModuleWise = cacheNames.split(",");
            for (String cacheNamePerModule : cacheNamesModuleWise) {
                String ttlCache = cacheValue.get(cacheNamePerModule + "." + CACHE_TTL_PREFIX);
                String cacheKeyPrefix = cacheValue.get(cacheNamePerModule + "." + CACHE_KEY_PREFIX);
                String setName=cacheValue.get(cacheNamePerModule+"."+CACHE_SET_NAME);
                String timeUnit = cacheValue.get(cacheNamePerModule + "." + CACHE_TIME_UNIT);
                TimeUnit timeUnit1=TimeUnit.SECONDS;
                if(!GeneralUtils.isNullOrEmpty(timeUnit)){
                    try {
                        timeUnit1=TimeUnit.valueOf(timeUnit);
                    }catch (Exception e){

                    }
                }
                long ttl = CACHE_TTL_DEFAULT_VALUE;
                if(ttlCache!=null){
                try {
                    ttl = Long.valueOf(ttlCache);
                } catch (Exception e) {

                }
                }
                CacheConfig cacheConfig = new CacheConfig(ttl, cacheKeyPrefix,setName,timeUnit1);
                cacheConfigMap.put(moduleName+"-"+cacheNamePerModule, cacheConfig);
            }
        }
    }

    public CacheConfig getCacheConfig(String moduleName){
        return cacheConfigMap.get(moduleName);
    }

    public Map<String, CacheConfig> getAllCacheConfigMap() {
        return cacheConfigMap;
    }
}
