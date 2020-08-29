package com.multimodule.cache;

import com.multimodule.cache.jvmcache.AppAwareJvmService;
import com.multimodule.cache.redis.AppAwareRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(name = "endpoints.refreshcache.enabled", havingValue = "true",
    matchIfMissing = true)
public class CacheManagementEndpointConfig {
  private static final Logger LOG = LoggerFactory.getLogger(CacheManagementEndpointConfig.class);

  @Bean
  public CacheManagementEndpoint cacheManagementEndpoint() {
    return new CacheManagementEndpoint();
  }

  @Endpoint(id = "refreshCache")
  static class CacheManagementEndpoint {

   @Autowired
    private AppAwareRedisService appAwareRedisService;

    @Autowired
    private AppAwareJvmService appAwareJvmService;

    public String invoke() {
        
      return "Refresh cache operation completed successfully for cache:";
    }
  }
}
