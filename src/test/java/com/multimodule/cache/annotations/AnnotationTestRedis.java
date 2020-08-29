package com.multimodule.cache.annotations;


import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.annotation.AnnotationBasedCacheHandler;
import com.multimodule.cache.cacheconfig.AppAwareCacheConfig;
import com.multimodule.cache.redis.RedisAppConfig;
import com.multimodule.cache.CommonCacheContainer;
import com.multimodule.cache.TestModuleNameProvider;
import com.multimodule.cache.utils.SpringFactoryUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        SpringFactoryUtil.class, RedisAppConfig.class,
        CommonCacheContainer.class,
        AppAwareCacheConfig.class,
        AnnotationBasedCacheHandler.class, TestWorkerEntity.class,
        TestService.class,
        TestModuleNameProvider.class})
@TestPropertySource(locations = {"classpath:redis.properties",
        "classpath:cachename.properties"})
public class AnnotationTestRedis {

    @Autowired
    @Qualifier("module1-redis")
    private IAppAwareCommonCacheOperations redisAeroSpikeCommonOperations;


    @Autowired
    private TestService testService;

    @Test
    public void testRedisAnnotation(){
        Counter counter=new Counter();
        testService.testRedisAnnotation(counter);
        String output = testService.testRedisAnnotation(counter);
        redisAeroSpikeCommonOperations.delete(Constants.SAMPLE_KEY);
        Object afterDeletion = redisAeroSpikeCommonOperations.get(Constants.SAMPLE_KEY);
        Assert.assertEquals(afterDeletion,null);
        Assert.assertEquals(counter.getCount(),1);
        Assert.assertEquals(output,Constants.SAMPLE_REDIS_OUTPUT);
    }
}
