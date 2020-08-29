package com.multimodule.cache.operations;

import com.multimodule.cache.CommonCacheContainer;
import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.TestModuleNameProvider;
import com.multimodule.cache.multimodule.ServiceModuleNameProvider;
import com.multimodule.cache.redis.AppAwareRedisService;
import com.multimodule.cache.redis.RedisAppConfig;
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
@ContextConfiguration(classes = {SpringFactoryUtil.class, RedisAppConfig.class,
        CommonCacheContainer.class, TestModuleNameProvider.class
})
@TestPropertySource(locations = "classpath:redis.properties")
public class RedisAppAwareCacheOperationsTest {

    @Autowired
    private ServiceModuleNameProvider serviceModuleNameProvider;

    @Autowired
    private AppAwareRedisService appAwareRedisService;

    private IAppAwareCommonCacheOperations iAppAwareCommonCacheOperations;



    @PostConstruct
    public void postConstruct(){
        iAppAwareCommonCacheOperations =
                appAwareRedisService.getAppAwareCacheOperations(serviceModuleNameProvider);
    }

    @Test
    public void testCommonOperations() throws InterruptedException {
        iAppAwareCommonCacheOperations.set(OperationsConstant.SAMPLE_KEY
                ,OperationsConstant.SAMPLE_REDIS_DATA,120, TimeUnit.SECONDS);
        String output = (String) iAppAwareCommonCacheOperations.get(OperationsConstant.SAMPLE_KEY);
        iAppAwareCommonCacheOperations.delete(OperationsConstant.SAMPLE_KEY);
        Object afterDeletion = iAppAwareCommonCacheOperations.get(OperationsConstant.SAMPLE_KEY);
        Assert.assertEquals(OperationsConstant.SAMPLE_REDIS_DATA,output);
        Thread.sleep(1000);
        Assert.assertEquals(null,afterDeletion);
    }


}
