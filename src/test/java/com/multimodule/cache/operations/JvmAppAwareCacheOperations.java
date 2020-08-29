package com.multimodule.cache.operations;

import com.multimodule.cache.CommonCacheContainer;
import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.TestModuleNameProvider;
import com.multimodule.cache.cacheconfig.AppAwareCacheConfig;
import com.multimodule.cache.jvmcache.AppAwareJvmService;
import com.multimodule.cache.jvmcache.JvmAppConfig;
import com.multimodule.cache.multimodule.ServiceModuleNameProvider;
import com.multimodule.cache.utils.SpringFactoryUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringFactoryUtil.class, JvmAppConfig.class,
        CommonCacheContainer.class, AppAwareCacheConfig.class, TestModuleNameProvider.class
})
@TestPropertySource(locations = "classpath:jvm.properties")
public class JvmAppAwareCacheOperations {


    @Autowired
    private ServiceModuleNameProvider serviceModuleNameProvider;

    @Autowired
    private AppAwareJvmService appAwareJvmService;

    private IAppAwareCommonCacheOperations iAppAwareCommonCacheOperations;



    @PostConstruct
    public void postConstruct(){
        iAppAwareCommonCacheOperations =
                appAwareJvmService.getAppAwareCacheOperations(serviceModuleNameProvider);
    }

    @Test
    public void testCommonOperations(){
        iAppAwareCommonCacheOperations.set(OperationsConstant.SAMPLE_KEY,
                OperationsConstant.SAMPLE_JVM_DATA,120, TimeUnit.SECONDS);
        String output = (String) iAppAwareCommonCacheOperations.get(OperationsConstant.SAMPLE_KEY);
        iAppAwareCommonCacheOperations.delete(OperationsConstant.SAMPLE_KEY);
        Object afterDeletion = iAppAwareCommonCacheOperations.get(OperationsConstant.SAMPLE_KEY);
        Assert.assertEquals(OperationsConstant.SAMPLE_JVM_DATA,output);
        Assert.assertEquals(afterDeletion,null);
    }
}
