package com.multimodule.cache.jvmcache;

import com.multimodule.cache.jvmcache.cacheoperations.IAppAwareJvmCommonTemplate;
import com.multimodule.cache.multimodule.ModuleAwarePropertiesUtils;
import com.multimodule.cache.CacheConstants;
import com.multimodule.cache.CommonCacheContainer;
import com.multimodule.cache.IAppAwareCommonCacheOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ComponentScan("com.ie.naukri.cache.jvmcache")
@DependsOn({"com.multimodule.cache.CommonCacheContainer"})
public class JvmAppConfig {

    private static final String JVM_PROP_PREFIX = "jvm.";

    private static final String LRU_MAP_SIZE="map.size";

    private static final String LRU_MAP_DEFAULT_SIZE="1000";

    @Autowired
    private Environment environment;


    @Bean(name = "jvm-service")
    public AppAwareJvmService appAwareJvmService() {
        final AppAwareJvmService appAwareJvmService = new AppAwareJvmService();
        // Get all properties starting with aerospike prefix
        final Map<String, Map<String, String>> moduleWiseAerospikeProps =
                ModuleAwarePropertiesUtils.readModuleWiseSubProperties(environment, JVM_PROP_PREFIX);

        for (Map.Entry<String, Map<String, String>> redisMapEntry : moduleWiseAerospikeProps
                .entrySet()) {
            final String moduleName = redisMapEntry.getKey();
            redisMapEntry.getValue().getOrDefault(LRU_MAP_SIZE,LRU_MAP_DEFAULT_SIZE);
            JvmCacheOperationsContainer jvmCacheOperationsContainer=new JvmCacheOperationsContainer(
                    Integer.valueOf(redisMapEntry.getValue().getOrDefault(LRU_MAP_SIZE,LRU_MAP_DEFAULT_SIZE)));
            IAppAwareCommonCacheOperations ngAppAwareJvmCommonOperations = new IAppAwareJvmCommonTemplate(jvmCacheOperationsContainer.getCacheMap());
            jvmCacheOperationsContainer.setiAppAwareCommonCacheOperations(
                    ngAppAwareJvmCommonOperations);
            appAwareJvmService.registerAppAwareCacheOperations(moduleName,jvmCacheOperationsContainer);
            CommonCacheContainer.register(moduleName+"-"+ CacheConstants.JVM_CLIENT,ngAppAwareJvmCommonOperations);
        }

        return appAwareJvmService;
    }

}
