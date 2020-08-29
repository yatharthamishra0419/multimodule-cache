package com.multimodule.cache.aerospike.templates.impl;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.WritePolicy;
import com.multimodule.cache.aerospike.templates.IAeroSpikeCommonTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AeroSpikeCommonTemplate<V> implements IAeroSpikeCommonTemplate<V> {

    private AerospikeClient aerospikeClient;

    protected static final String DEFAULT_BIN_NAME = "bin";

    private String namespace;

    public AeroSpikeCommonTemplate(AerospikeClient aerospikeClient, String namespace) {
        this.aerospikeClient = aerospikeClient;
        this.namespace = namespace;
    }


    public List<V> multiGet(String cacheName, Collection<String> keys) {
        final Key[] aKeys =
                keys.stream().map(key -> new Key(this.namespace, cacheName, key)).toArray(Key[]::new);
        return getFromMultiBin(cacheName, aKeys, DEFAULT_BIN_NAME);
    }

    protected List<V> getFromMultiBin(String cacheName, Key[] keys, String binName) {
        final Record[] records = aerospikeClient.get(null, keys, binName);

        final List<V> values = new ArrayList<>();
        for (Record record : records) {
            values.add(record == null ? null : (V) record.bins.get(binName));
        }

        return values;
    }

    public void set(String cacheName, String key, V value, long timeout, TimeUnit unit) {
        final Key aKey = new Key(this.namespace, cacheName, key);

        final WritePolicy writePolicy = new WritePolicy(aerospikeClient.writePolicyDefault);
        writePolicy.expiration = (int) unit.toSeconds(timeout);

        aerospikeClient.put(writePolicy, aKey, new Bin(DEFAULT_BIN_NAME, value));
    }

    public void delete(String cacheName, Collection<String> keys) {
        Iterator var3 = keys.iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            this.aerospikeClient.delete((WritePolicy)null, new Key(namespace, cacheName, key));
        }

    }
}
