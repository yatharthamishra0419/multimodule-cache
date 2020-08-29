package com.multimodule.cache.annotations;

import com.multimodule.cache.CacheConstants;
import com.multimodule.cache.CommonCacheContainer;
import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.TestModuleNameProvider;
import com.multimodule.cache.aerospike.AeroSpikeAppConfig;
import com.multimodule.cache.annotation.AnnotationBasedCacheHandler;
import com.multimodule.cache.cacheconfig.AppAwareCacheConfig;
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
        SpringFactoryUtil.class,
        AeroSpikeAppConfig.class,
        CommonCacheContainer.class,
        AppAwareCacheConfig.class,
        AnnotationBasedCacheHandler.class,
        TestWorkerEntity.class,
        TestService.class,
        TestModuleNameProvider.class})
@TestPropertySource(locations = {
        "classpath:aerospike.properties",
        "classpath:cachename.properties"})
public class AnnotationTestAeroSpike {

    @Autowired
    private TestService testService;

    @Autowired
    @Qualifier("module1-aerospike")
    private IAppAwareCommonCacheOperations aeroSpikeCommonCacheOperations;

    @Test
    public void testAeroSpikeAnnotation(){
        Counter counter=new Counter();
        testService.testAeroSpike(counter);
        String output = testService.testAeroSpike(counter);
        aeroSpikeCommonCacheOperations.delete(Constants.SET_NAME +
                CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR
                +Constants.SAMPLE_KEY); // setname-keyname
        Object afterDeletion = aeroSpikeCommonCacheOperations.get(Constants.SET_NAME
                + CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR
                + Constants.SAMPLE_KEY);
        Assert.assertEquals(counter.getCount(),1);
        Assert.assertEquals(output,Constants.SAMPLE_AEROSPIKE_OUTPUT);
        Assert.assertEquals(afterDeletion,null);
    }


}
