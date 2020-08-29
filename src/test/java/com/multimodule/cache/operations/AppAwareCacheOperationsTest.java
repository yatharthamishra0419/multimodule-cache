package com.multimodule.cache.operations;

import com.multimodule.cache.CacheConstants;
import com.multimodule.cache.CommonCacheContainer;
import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.TestModuleNameProvider;
import com.multimodule.cache.aerospike.AeroSpikeAppConfig;
import com.multimodule.cache.aerospike.AppAwareAerospikeService;
import com.multimodule.cache.multimodule.ServiceModuleNameProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AeroSpikeAppConfig.class,
        CommonCacheContainer.class, TestModuleNameProvider.class
        })
@TestPropertySource(locations = "classpath:aerospike.properties")
public class AppAwareCacheOperationsTest {

  private IAppAwareCommonCacheOperations aeroSpikeCommonCacheOperations;

  @Autowired
  private AppAwareAerospikeService appAwareAerospikeService;

  @Autowired
  private ServiceModuleNameProvider serviceModuleNameProvider;

  @PostConstruct
  public void postConstruct(){
    aeroSpikeCommonCacheOperations=
            appAwareAerospikeService.getAppAwareCacheOperations(serviceModuleNameProvider);
  }


  @Test
  public void testCommonOperations(){
    aeroSpikeCommonCacheOperations.set(OperationsConstant.SET_NAME+
                    CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR+
                    OperationsConstant.SAMPLE_KEY,
            OperationsConstant.SAMPLE_AEROSPIKE_DATA,2000, TimeUnit.SECONDS);
    String output = (String)aeroSpikeCommonCacheOperations.get(
            OperationsConstant.SET_NAME+CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR
                    +OperationsConstant.SAMPLE_KEY);
    aeroSpikeCommonCacheOperations.delete(OperationsConstant.SET_NAME
            +CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR
            +OperationsConstant.SAMPLE_KEY);
    Object afterDeletion
            = aeroSpikeCommonCacheOperations.get(OperationsConstant.SET_NAME
            + CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR
            + OperationsConstant.SAMPLE_KEY);
    assertEquals(output,OperationsConstant.SAMPLE_AEROSPIKE_DATA);
    assertEquals(afterDeletion,null);
  }




}
