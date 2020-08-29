package com.multimodule.cache.aerospike;

import com.aerospike.client.policy.ClientPolicy;
import com.multimodule.cache.IAppAwareCommonCacheOperations;

public class AeroSpikeOperationContainer {

    private String clusterNodes;
    private ClientPolicy clientPolicy;

    private IAppAwareCommonCacheOperations<?> aerospikeCacheOperations;

    public AeroSpikeOperationContainer(){

    }

    public AeroSpikeOperationContainer(String clusterNodes, ClientPolicy clientPolicy, IAppAwareCommonCacheOperations aerospikeCacheOperations){
        this.clusterNodes=clusterNodes;
        this.clientPolicy=clientPolicy;
        this.aerospikeCacheOperations=aerospikeCacheOperations;
    }

    public IAppAwareCommonCacheOperations<?> getAerospikeCacheOperations() {
        return aerospikeCacheOperations;
    }

    public void setAerospikeCacheOperations(IAppAwareCommonCacheOperations<?> aerospikeCacheOperations) {
        this.aerospikeCacheOperations = aerospikeCacheOperations;
    }

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public ClientPolicy getClientPolicy() {
        return clientPolicy;
    }

    public void setClientPolicy(ClientPolicy clientPolicy) {
        this.clientPolicy = clientPolicy;
    }
}
