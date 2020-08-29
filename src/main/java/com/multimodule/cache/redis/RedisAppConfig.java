package com.multimodule.cache.redis;

import com.multimodule.cache.multimodule.ModuleAwarePropertiesUtils;
import com.multimodule.cache.redis.cacheoperations.IAppAwareRedisTemplate;
import com.multimodule.cache.CacheConstants;
import com.multimodule.cache.CommonCacheContainer;
import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.utils.GeneralUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ComponentScan("com.ie.naukri.cache.redis")
@DependsOn({"com.multimodule.cache.CommonCacheContainer"})
public class RedisAppConfig {

    private static final String REDIS_PROP_PREFIX = "redis.";

    Logger logger ;

    @Autowired
    private Environment environment;

    public RedisAppConfig(){
        logger= LoggerFactory.getLogger(RedisAppConfig.class);
    }


    @Bean(name = "redis-service")
    public AppAwareRedisService appAwareRedisService() {
        final AppAwareRedisService appAwareCacheService = new AppAwareRedisService();
        // Get all properties starting with redis prefix
        final Map<String, Map<String, String>> moduleWiseAerospikeProps =
                ModuleAwarePropertiesUtils.readModuleWiseSubProperties(environment, REDIS_PROP_PREFIX);

        for (Map.Entry<String, Map<String, String>> redisMapEntry : moduleWiseAerospikeProps
                .entrySet()) {
            final String moduleName = redisMapEntry.getKey();
            final Map<String, String> redisProps = redisMapEntry.getValue();
            RedisProperties redisProperties
                    = RedisPropertiesBuilder.getCacheProperties(redisProps);
            Pool<Jedis> jedisPool = getJedisPool(redisProperties);
            final RedisOperationsContainer opsContainer = new RedisOperationsContainer();
            opsContainer.setClusterNodes(redisProperties.getEndPoint());
            opsContainer.setJedisPool(jedisPool);
            opsContainer.setRedisProperties(redisProperties);
            IAppAwareCommonCacheOperations ngAppAwareRedisTemplate = getNGAppAwareRedisTemplate(jedisPool);
            opsContainer.setRedisCacheOperations(ngAppAwareRedisTemplate);
            appAwareCacheService.registerAppAwareCacheOperations(moduleName, opsContainer);
            CommonCacheContainer.register(moduleName+"-"+ CacheConstants.REDIS_CLIENT
                    ,ngAppAwareRedisTemplate);
        }

        return appAwareCacheService;
    }

    private IAppAwareCommonCacheOperations getNGAppAwareRedisTemplate(Pool<Jedis> jedisPool){
        IAppAwareRedisTemplate ngAppAwareRedisTemplate=new IAppAwareRedisTemplate(jedisPool);

        return ngAppAwareRedisTemplate;
    }

    private Pool<Jedis> getJedisPool(RedisProperties redisProperties){
        Pool<Jedis> jedisPool=null;
        if (GeneralUtils.isNullOrEmpty(redisProperties.getEndPoint())) {
            logger.warn("Redis End point is not defined.So bypass redis cache pool object creation.");
            return null;
        }

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(redisProperties.getMaxTotalConnections());
        genericObjectPoolConfig.setTestWhileIdle(true);
        genericObjectPoolConfig.setTestOnBorrow(true);
        genericObjectPoolConfig.setTestOnReturn(true);
        genericObjectPoolConfig.setMinIdle(redisProperties.getMinIdleConnections());
        genericObjectPoolConfig.setMaxWaitMillis(redisProperties.getMaxwaitmillis());

        String[] nodes = redisProperties.getEndPoint().split(",");

        if (nodes.length == 1) {
            if (nodes[0].contains(":")) {
                String host = nodes[0].split(":")[0];
                int port = Integer.parseInt(nodes[0].split(":")[1]);
                if (GeneralUtils.isNullOrEmpty(redisProperties.getPassword())) {
                    jedisPool = new JedisPool(genericObjectPoolConfig, host, port,
                            redisProperties.getDefaultTimeoutInMillis());
                } else {
                    jedisPool = new JedisPool(genericObjectPoolConfig, host, port,
                            redisProperties.getDefaultTimeoutInMillis(), redisProperties.getPassword());
                }
            } else {
                logger.error(
                        "Redis client can not be initialized because host name and port are not defined correctly");
                return null;
            }
        } else {
            HashSet<String> sentinels = new HashSet<>();
            sentinels.addAll(Arrays.asList(nodes));
            if (GeneralUtils.isNullOrEmpty(redisProperties.getPassword())) {
                jedisPool = new JedisSentinelPool(redisProperties.getMasterName(), sentinels,
                        genericObjectPoolConfig, redisProperties.getDefaultTimeoutInMillis());
            } else {
                jedisPool = new JedisSentinelPool(redisProperties.getMasterName(), sentinels,
                        genericObjectPoolConfig, redisProperties.getDefaultTimeoutInMillis(),
                        redisProperties.getPassword());
            }

        }
        return jedisPool;
    }


}
