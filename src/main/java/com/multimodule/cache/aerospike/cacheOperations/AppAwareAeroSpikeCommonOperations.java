package com.multimodule.cache.aerospike.cacheOperations;

import com.aerospike.client.AerospikeClient;
import com.multimodule.cache.aerospike.templates.impl.AeroSpikeCommonTemplate;
import com.multimodule.cache.CacheConstants;
import com.multimodule.cache.IAppAwareCommonCacheOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AppAwareAeroSpikeCommonOperations<V> extends AeroSpikeCommonTemplate implements IAppAwareCommonCacheOperations<V> {


    private AerospikeClient aerospikeClient;

    private String namespace;

    public AppAwareAeroSpikeCommonOperations(AerospikeClient aerospikeClient, String namespace){
        super(aerospikeClient,namespace);
        this.aerospikeClient=aerospikeClient;
        this.namespace=namespace;
    }


    @Override
    public Map<String, V> multiget(String[] cacheKeys) {
        List<AeroSpikeObject> aeroSpikeObjects=new LinkedList<>();
        for(String key:cacheKeys){
            aeroSpikeObjects.add(AeroSpikeObject.getInstance(key));
        }
        Map<String,List<String>> mapSetToKeys=new HashMap<>();
        Map<String,V> cacheResultMap=new LinkedHashMap<>();
        for(AeroSpikeObject aeroSpikeObject:aeroSpikeObjects){
            if(mapSetToKeys.get(aeroSpikeObject.getSetName())==null)
                mapSetToKeys.put(aeroSpikeObject.getSetName(),new ArrayList<>());
            mapSetToKeys.get(aeroSpikeObject.getSetName()).add(aeroSpikeObject.getKey());
        }
        for(Map.Entry<String,List<String>> entry:mapSetToKeys.entrySet()){
            List<String> keys = entry.getValue();
            String setName=entry.getKey();
            List<V> values = this.multiGet(setName, keys);
            int index=0;
            String prefix=entry.getKey().equals("")?"":
                    entry.getKey()+ CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR;
            for(String key:entry.getValue()){
                cacheResultMap.put(prefix+key,values.get(index));
                index++;
            }

        }
        return cacheResultMap;
    }

    @Override
    public void set(String key, V data, long ttl,TimeUnit unit) {
        if(key==null)
            throw new NullPointerException("key cannot be null");
        AeroSpikeObject aeroSpikeObject = AeroSpikeObject.getInstance(key);
        this.set(aeroSpikeObject.getSetName(),aeroSpikeObject.getKey(),data,ttl, unit);
    }

    @Override
    public void multidelete(String[] keys) {
        if(keys==null)
            return;
        List<AeroSpikeObject> aeroSpikeObjects=new LinkedList<>();
        for(String key:keys){
            aeroSpikeObjects.add(AeroSpikeObject.getInstance(key));
        }

        Map<AeroSpikeObject,List<String>> setToKeyMapping=new HashMap<>();
        for(AeroSpikeObject aeroSpikeObject:aeroSpikeObjects){
            if(setToKeyMapping.get(aeroSpikeObject)==null){
                setToKeyMapping.put(aeroSpikeObject,new LinkedList<>());
            }
            setToKeyMapping.get(aeroSpikeObject).add(aeroSpikeObject.getKey());
        }
        for(Map.Entry<AeroSpikeObject,List<String>> entry:setToKeyMapping.entrySet()){
            this.delete(entry.getKey().getSetName(),entry.getValue());
        }
    }



    @Override
    public V get(String cacheKey) {
        String[] key=new String[1];
        key[0]=cacheKey;
        Map<String, V> multiget = multiget(key);
        if(multiget.entrySet().iterator().next()==null)
            return (V)"";
        return multiget.entrySet().iterator().next().getValue();
    }

    @Override
    public void delete(String cacheKey) {
        String[] key=new String[1];
        key[0]=cacheKey;
        multidelete(key);
    }
}
class AeroSpikeObject{
    String key="";
    String setName="";

    public AeroSpikeObject(String setName, String key) {
        this.key = key;
        this.setName = setName;
    }

    public AeroSpikeObject(String key) {
        this.key = key;
    }

    public static AeroSpikeObject getInstance(String setsAndKeys){
        String[] setsAndKey = setsAndKeys.split(CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR);
        AeroSpikeObject aeroSpikeObject;
        if(setsAndKey.length==2)
            aeroSpikeObject=new AeroSpikeObject(setsAndKey[0],setsAndKey[1]);
        else
            aeroSpikeObject=new AeroSpikeObject(CacheConstants.DEFAULT_SET_NAME,setsAndKey[0]);
        return aeroSpikeObject;
    }

    private AeroSpikeObject(){

    }

    public String getKey() {
        return key;
    }

    public String getSetName() {
        return setName;
    }
}
