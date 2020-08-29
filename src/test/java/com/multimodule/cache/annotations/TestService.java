package com.multimodule.cache.annotations;

import com.multimodule.cache.CacheConstants;
import com.multimodule.cache.TestModuleNameProvider;
import com.multimodule.cache.annotation.MultiModuleCachable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired
    private TestWorkerEntity testWorkerEntity;

    @MultiModuleCachable(cacheName = "test",
            serviceModuleNameProvider = TestModuleNameProvider.class,
            cacheType = CacheConstants.REDIS_CLIENT,
    cacheKey = "[0].getSampleAnnotationKey()")
    public String testRedisAnnotation(Counter counter){
        return testWorkerEntity.testRedisWorker(counter);
    }

    @MultiModuleCachable(cacheName = "test",
            serviceModuleNameProvider = TestModuleNameProvider.class,
            cacheType = CacheConstants.AEROSPIKE_CLIENT,
            cacheKey = "[0].getSampleAnnotationKey()")
    public String testAeroSpike(Counter counter){
        return testWorkerEntity.testAeroSpikeWorker(counter);
    }

    @MultiModuleCachable(cacheName = "test",
            serviceModuleNameProvider =
                    TestModuleNameProvider.class,
            cacheType = CacheConstants.JVM_CLIENT,
            cacheKey = "[0].getSampleAnnotationKey()")
    public String testJvm(Counter counter){
        return testWorkerEntity.testJvmWorker(counter);
    }
}
