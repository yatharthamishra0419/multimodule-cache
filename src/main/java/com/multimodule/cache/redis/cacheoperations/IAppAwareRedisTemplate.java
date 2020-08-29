package com.multimodule.cache.redis.cacheoperations;

import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.utils.ByteConverterUtil;
import com.multimodule.cache.utils.GeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class IAppAwareRedisTemplate implements IAppAwareCommonCacheOperations {

    private Pool<Jedis> jedisPool;

    Logger logger;

    public IAppAwareRedisTemplate(Pool<Jedis> jedisPool) {
        logger=LoggerFactory.getLogger(IAppAwareRedisTemplate.class);
        this.jedisPool = jedisPool;
    }



    @Override
    public Map<String, ?> multiget(String[] cacheKeys) {
        Jedis jedis = null;

        List<byte[]> response;
        try {
            jedis = jedisPool.getResource();
            byte[][] byteList = new byte[cacheKeys.length][];
            int index = 0;
            for (String cacheKey : cacheKeys) {
                byte[] keyByte = ByteConverterUtil.convertToBytes(cacheKey);
                byteList[index] = keyByte;
                index++;
            }

            response = jedis.mget(byteList);

        } finally {
            if (jedis != null) {
                jedis.close();
            } else {
                logger.error("Jedis client is null in get");
            }
        }
        if(GeneralUtils.isNullOrEmpty(response))
            return null;
        Map<String, Object> outputValueMap = new LinkedHashMap<>();
        int index = 0;
        for (String cache : cacheKeys) {
            byte[] bytes = response.get(index);
            index++;
            if(bytes==null){
                outputValueMap.put(cache,null);
                continue;
            }
            outputValueMap.put(cache,
                    ByteConverterUtil.getObject(bytes));
        }
        return outputValueMap;
    }

    @Override
    public void set(String key, Object data, long ttl, TimeUnit unit) {
        byte[] bytes = ByteConverterUtil.convertToBytes(data);
        byte[] keyBytes = ByteConverterUtil.convertToBytes(key);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (data != null) {
                jedis.set(keyBytes, bytes);
                if (ttl > 0) {
                    jedis.expire(keyBytes, (int) unit.toSeconds(ttl));
                }
            } else {
                jedis.del(keyBytes);
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            } else {
                logger.error("Jedis client is null in set");
            }
        }
    }

    @Override
    public void multidelete(String[] cacheKeys) {
        Jedis jedis = null;
        byte[][] byteList = new byte[cacheKeys.length][];
        int index = 0;
        for (String cacheKey : cacheKeys) {
            byte[] keyByte = ByteConverterUtil.convertToBytes(cacheKey);
            byteList[index] = keyByte;
            index++;
        }
        try {
            jedis = jedisPool.getResource();
            jedis.del(byteList);
        } finally {
            if (jedis != null) {
                jedis.close();
            } else {
                logger.error("Jedis client is null in delete");
            }
        }
    }

    @Override
    public Object get(String cacheKey) {
        String[] key = new String[1];
        key[0] = cacheKey;
        Map<String, ?> multiget = multiget(key);
        return multiget.get(cacheKey);
    }

    @Override
    public void delete(String cacheKey) {
        String[] key = new String[1];
        key[0] = cacheKey;
        multidelete(key);
    }

}
