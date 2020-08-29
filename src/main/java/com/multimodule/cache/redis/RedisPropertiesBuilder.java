package com.multimodule.cache.redis;

/*
 author : nitin.goyal
*/

import com.multimodule.cache.CacheConstants;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Map;


@DependsOn({"propertyManager"})
public abstract class RedisPropertiesBuilder {

  private String REDIS_CONF_PREFIX="redis.";
  private static final String AEROSPIKE_PROP_ENABLED = "aerospike.cache.enabled";

  private static  Map<String,Object> defaultValues=new HashMap<>();

  static {
    defaultValues.put(CacheConstants.ENDPOINT_SUFFIX,"");
    defaultValues.put(CacheConstants.MAX_TOTAL_CONNECTION_SUFFIX,"1000");
    defaultValues.put(CacheConstants.MIN_IDLE_CONNECTION_SUFFIX,"10");
    defaultValues.put(CacheConstants.DEFAULT_TIMEOUT_MILLIS_SUFFIX,"200");
    defaultValues.put(CacheConstants.MAX_WAIT_IN_MILLIS_SUFFIX,"1200");
  }

  private static Object getDefaultValueOrNull(Map<String, String> redisProps,String redisProp){
    if(redisProps.get(redisProp)==null)
      return (defaultValues.get(redisProp)==null?"":defaultValues.get(redisProp));
    return redisProps.get(redisProp);
  }

  public static RedisProperties getCacheProperties(Map<String, String> redisProps) {
    RedisProperties redisProperties = new RedisProperties();
    redisProperties.setEndPoint((String) getDefaultValueOrNull(redisProps,CacheConstants.ENDPOINT_SUFFIX));
    redisProperties.setMasterName((String) getDefaultValueOrNull(redisProps,CacheConstants.MASTER));
    redisProperties.setPassword((String) getDefaultValueOrNull(redisProps,CacheConstants.PASSWORD_SUFFIX));
    redisProperties.setMaxTotalConnections(Integer.valueOf((String) getDefaultValueOrNull(redisProps,CacheConstants.MAX_TOTAL_CONNECTION_SUFFIX)));
    redisProperties.setMinIdleConnections(Integer.valueOf((String) getDefaultValueOrNull(redisProps,CacheConstants.MIN_IDLE_CONNECTION_SUFFIX)));
    redisProperties.setDefaultTimeoutInMillis(Integer.valueOf((String) getDefaultValueOrNull(redisProps,CacheConstants.DEFAULT_TIMEOUT_MILLIS_SUFFIX)));
    redisProperties.setMaxwaitmillis(Integer.valueOf((String) getDefaultValueOrNull(redisProps,CacheConstants.MAX_WAIT_IN_MILLIS_SUFFIX)));
    return redisProperties;
  }
}
