package com.multimodule.cache.annotations;

import org.springframework.stereotype.Service;

@Service
public class TestWorkerEntity {

    public String testRedisWorker(Counter counter){
        counter.increment();
       return Constants.SAMPLE_REDIS_OUTPUT;
    }

    public String testAeroSpikeWorker(Counter counter){
        counter.increment();
        return Constants.SAMPLE_AEROSPIKE_OUTPUT;
    }

    public String testJvmWorker(Counter counter){
        counter.increment();
        return Constants.SAMPLE_JVM_OUTPUT;
    }
}
