package com.multimodule.cache.annotations;


import com.multimodule.cache.CommonCacheContainer;
import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.TestModuleNameProvider;
import com.multimodule.cache.annotation.AnnotationBasedCacheHandler;
import com.multimodule.cache.cacheconfig.AppAwareCacheConfig;
import com.multimodule.cache.jvmcache.JvmAppConfig;
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
        SpringFactoryUtil.class, JvmAppConfig.class,
        CommonCacheContainer.class,
        AppAwareCacheConfig.class,
        AnnotationBasedCacheHandler.class, TestWorkerEntity.class,
        TestService.class,
        TestModuleNameProvider.class})
@TestPropertySource(locations = {
        "classpath:jvm.properties",
        "classpath:cachename.properties"})
public class AnnotationTestJvm {

    @Autowired
    @Qualifier("module1-jvm")
    private IAppAwareCommonCacheOperations jvmCommonOperations;


    @Autowired
    private TestService testService;


    @Test
    public void testJvm(){
        Counter counter=new Counter();
        testService.testJvm(counter);
        String output = testService.testJvm(counter);
        jvmCommonOperations.delete(Constants.SAMPLE_KEY);
        Object afterDeletion = jvmCommonOperations.get(Constants.SAMPLE_KEY);
        Assert.assertEquals(counter.getCount(),1);
        Assert.assertEquals(output,Constants.SAMPLE_JVM_OUTPUT);
        Assert.assertEquals(afterDeletion,null);
    }
}
