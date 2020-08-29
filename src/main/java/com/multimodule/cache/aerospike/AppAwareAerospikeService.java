package com.multimodule.cache.aerospike;

import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.multimodule.ModuleAwarePropertiesUtils;
import com.multimodule.cache.multimodule.ServiceModuleNameProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AppAwareAerospikeService {

    private ConcurrentMap<String, AeroSpikeOperationContainer> moduleWiseCacheOpsContainer =
            new ConcurrentHashMap<>();

    public void registerAppAwareCacheOperations(String moduleName,
                                                AeroSpikeOperationContainer appAwareCacheOperationsContainer) {
        this.moduleWiseCacheOpsContainer.put(moduleName, appAwareCacheOperationsContainer);
    }

    public AppAwareAerospikeService() {
        System.out.println("inside inside");
    }

    private AeroSpikeOperationContainer getContainer(
            ServiceModuleNameProvider moduleNameProvider) {
        String moduleName = ModuleAwarePropertiesUtils.getModuleName(moduleNameProvider);
        final AeroSpikeOperationContainer opsContainer =
                this.moduleWiseCacheOpsContainer.get(moduleName);
        if (opsContainer == null) {
            throw new IllegalStateException("No configuration found for module with name: " + moduleName);
        }
        return opsContainer;
    }

    public <V> IAppAwareCommonCacheOperations<V> getAppAwareCacheOperations(
            ServiceModuleNameProvider moduleNameProvider) {
        final AeroSpikeOperationContainer opsContainer = getContainer(moduleNameProvider);
        return (IAppAwareCommonCacheOperations<V>) opsContainer.getAerospikeCacheOperations();
    }
}
