package com.multimodule.cache.redis;

import com.multimodule.cache.multimodule.ModuleAwarePropertiesUtils;
import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.multimodule.ServiceModuleNameProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AppAwareRedisService {

    private  ConcurrentMap<String, RedisOperationsContainer> moduleWiseCacheOpsContainer =
            new ConcurrentHashMap<>();

    public void registerAppAwareCacheOperations(String moduleName,
                                                RedisOperationsContainer appAwareCacheOperationsContainer) {
        this.moduleWiseCacheOpsContainer.put(moduleName, appAwareCacheOperationsContainer);
    }

    public AppAwareRedisService() {

    }

    private RedisOperationsContainer getContainer(
            ServiceModuleNameProvider moduleNameProvider) {
        String moduleName = ModuleAwarePropertiesUtils.getModuleName(moduleNameProvider);
        final RedisOperationsContainer opsContainer =
                this.moduleWiseCacheOpsContainer.get(moduleName);
        if (opsContainer == null) {
            throw new IllegalStateException("No configuration found for module with name: " + moduleName);
        }
        return opsContainer;
    }

    public <V> IAppAwareCommonCacheOperations<V> getAppAwareCacheOperations(
            ServiceModuleNameProvider moduleNameProvider) {
        final RedisOperationsContainer opsContainer = getContainer(moduleNameProvider);
        return (IAppAwareCommonCacheOperations<V>) opsContainer.getRedisCacheOperations();
    }
}
