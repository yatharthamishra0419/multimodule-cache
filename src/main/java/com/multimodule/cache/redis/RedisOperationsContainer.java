package com.multimodule.cache.redis;

import com.multimodule.cache.IAppAwareCommonCacheOperations;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

public class RedisOperationsContainer {

    private String clusterNodes;
    private Pool<Jedis> jedisPool;
    private RedisProperties redisProperties;
    private IAppAwareCommonCacheOperations<?> redisCacheOperations;

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public RedisProperties getRedisProperties() {
        return redisProperties;
    }

    public void setRedisProperties(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    public IAppAwareCommonCacheOperations<?> getRedisCacheOperations() {
        return redisCacheOperations;
    }

    public void setRedisCacheOperations(IAppAwareCommonCacheOperations<?> redisCacheOperations) {
        this.redisCacheOperations = redisCacheOperations;
    }

    public Pool<Jedis> getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
    }
}
