package com.multimodule.cache.jvmcache;

import com.multimodule.cache.multimodule.ModuleAwarePropertiesUtils;
import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.multimodule.ServiceModuleNameProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AppAwareJvmService {

    private final ConcurrentMap<String, JvmCacheOperationsContainer> moduleWiseCacheOpsContainer =
            new ConcurrentHashMap<>();

    public void registerAppAwareCacheOperations(String moduleName,
                                                JvmCacheOperationsContainer appAwareCacheOperationsContainer) {
        this.moduleWiseCacheOpsContainer.put(moduleName, appAwareCacheOperationsContainer);
    }

    public AppAwareJvmService() {

    }

    private JvmCacheOperationsContainer getContainer(
            ServiceModuleNameProvider moduleNameProvider) {
        String moduleName = ModuleAwarePropertiesUtils.getModuleName(moduleNameProvider);
        final JvmCacheOperationsContainer opsContainer =
                this.moduleWiseCacheOpsContainer.get(moduleName);
        if (opsContainer == null) {
            throw new IllegalStateException("No configuration found for module with name: " + moduleName);
        }
        return opsContainer;
    }

    public <V> IAppAwareCommonCacheOperations<V> getAppAwareCacheOperations(
            ServiceModuleNameProvider moduleNameProvider) {
        final JvmCacheOperationsContainer opsContainer = getContainer(moduleNameProvider);
        return (IAppAwareCommonCacheOperations<V>) opsContainer.getiAppAwareCommonCacheOperations();
    }
}
