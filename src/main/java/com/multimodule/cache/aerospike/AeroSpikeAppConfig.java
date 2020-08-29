package com.multimodule.cache.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.client.policy.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multimodule.cache.CacheConstants;
import com.multimodule.cache.CommonCacheContainer;
import com.multimodule.cache.DummyCommonOperations;
import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.aerospike.cacheOperations.AppAwareAeroSpikeCommonOperations;
import com.multimodule.cache.multimodule.ModuleAwarePropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ComponentScan("com.ie.naukri.aerospike")
@DependsOn({"com.multimodule.cache.CommonCacheContainer"})
public class AeroSpikeAppConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AeroSpikeAppConfig.class);

    private static final String AEROSPIKE_PROP_PREFIX = "aerospike.";
    private static final String AEROSPIKE_POLICY_PROP_PREFIX = "policy.";

    private static final String AEROSPIKE_PROP_NAMESPACE = "namespace";
    private static final String AEROSPIKE_PROP_CLUSTER_NODES = "cluster.nodes";
    private static final String AEROSPIKE_PROP_DISABLED = "cache.disabled";
    private static final String AEROSPIKE_PROP_TIMEOUT = "cache.timeout";

    private final ObjectMapper objectMapper =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private Environment environment;

    private <T> T getPolicy(Map<String, String> clientPolicyAttrs, String policyPrefix,
                            Class<T> clazz) {
        final Map<String, String> policySpecificAttrs = ModuleAwarePropertiesUtils
                .getRequiredSubPropertiesFromSourceProps(policyPrefix, clientPolicyAttrs);

        // Object mapper is used as Policy objects don't have getters/setters and hence BeanUtils
        // doesn't work
        return objectMapper.convertValue(policySpecificAttrs, clazz);
    }

    private ClientPolicy getClientPolicy(Map<String, String> clientPolicyAttrs,
                                         String defaultTimeout) {
        final Map<String, String> defaultClientPolicyAttrs = new HashMap<>();
        defaultClientPolicyAttrs.put("timeout", defaultTimeout);
        defaultClientPolicyAttrs.put("readPolicyDefault.totalTimeout", defaultTimeout);
        defaultClientPolicyAttrs.put("writePolicyDefault.totalTimeout", defaultTimeout);
        defaultClientPolicyAttrs.put("scanPolicyDefault.totalTimeout", defaultTimeout);
        defaultClientPolicyAttrs.put("queryPolicyDefault.totalTimeout", defaultTimeout);
        defaultClientPolicyAttrs.put("batchPolicyDefault.totalTimeout", defaultTimeout);
        defaultClientPolicyAttrs.put("infoPolicyDefault.timeout", defaultTimeout);

        setSocketTimeoutAndMaxRetry(defaultTimeout, defaultClientPolicyAttrs);

        defaultClientPolicyAttrs.putAll(clientPolicyAttrs);

        // Object mapper is used as ClientPolicy doesn't have getters/setters and hence BeanUtils
        // doesn't work
        final ClientPolicy clientPolicy =
                objectMapper.convertValue(defaultClientPolicyAttrs, ClientPolicy.class);
        clientPolicy.readPolicyDefault =
                getPolicy(defaultClientPolicyAttrs, "readPolicyDefault.", Policy.class);
        clientPolicy.writePolicyDefault =
                getPolicy(defaultClientPolicyAttrs, "writePolicyDefault.", WritePolicy.class);
        clientPolicy.scanPolicyDefault =
                getPolicy(defaultClientPolicyAttrs, "scanPolicyDefault.", ScanPolicy.class);
        clientPolicy.queryPolicyDefault =
                getPolicy(defaultClientPolicyAttrs, "queryPolicyDefault.", QueryPolicy.class);
        clientPolicy.batchPolicyDefault =
                getPolicy(defaultClientPolicyAttrs, "batchPolicyDefault.", BatchPolicy.class);
        clientPolicy.infoPolicyDefault =
                getPolicy(defaultClientPolicyAttrs, "infoPolicyDefault.", InfoPolicy.class);

        return clientPolicy;
    }

    private void setSocketTimeoutAndMaxRetry(String defaultTimeout,
                                             final Map<String, String> defaultClientPolicyAttrs) {

        Integer maxRetry = Integer.valueOf(3);
        Integer defaultTimeoutInt = Integer.parseInt(defaultTimeout);
        Integer socketTimeout = defaultTimeoutInt / maxRetry;

        defaultClientPolicyAttrs.put("readPolicyDefault.maxRetries", maxRetry.toString());
        defaultClientPolicyAttrs.put("writePolicyDefault.maxRetries", maxRetry.toString());

        defaultClientPolicyAttrs.put("readPolicyDefault.socketTimeout", socketTimeout.toString());
        defaultClientPolicyAttrs.put("writePolicyDefault.socketTimeout", socketTimeout.toString());
    }


    private AerospikeClient getAerospikeClient(String clusterNodes, ClientPolicy clientPolicy) {
        // Resolve dns names for IP addresses
        final Host[] hosts = Arrays.stream(clusterNodes.split("\\s*,\\s*"))
                // Check whether node is not empty
                .filter(node -> !StringUtils.isEmpty(node))
                // Resolve DNS with IP and port
                // Convert resolved DNS to Aerospike Host class
                .flatMap(resolvedDNS -> Arrays.stream(Host.parseHosts(resolvedDNS, 3000)))
                .toArray(Host[]::new);

        return new AerospikeClient(clientPolicy, hosts);
    }


    private IAppAwareCommonCacheOperations<?> getAppAwareAerospikeCommonOperations(AerospikeClient aerospikeClient,
                                                                                   String aerospikeNamespace) {

        final IAppAwareCommonCacheOperations<Object> appAwareAerospikeCommonOperation =
                new AppAwareAeroSpikeCommonOperations<>(aerospikeClient, aerospikeNamespace);

        return appAwareAerospikeCommonOperation;
    }


    @Bean(name = "aerospike-service")
    public AppAwareAerospikeService appAwareAerospikeService() {
        final AppAwareAerospikeService appAwareCacheService = new AppAwareAerospikeService();
        // Get all properties starting with aerospike prefix
        final Map<String, Map<String, String>> moduleWiseAerospikeProps =
                ModuleAwarePropertiesUtils.readModuleWiseSubProperties(environment, AEROSPIKE_PROP_PREFIX);

        for (Map.Entry<String, Map<String, String>> aSpikePropsEntry : moduleWiseAerospikeProps
                .entrySet()) {
            final String moduleName = aSpikePropsEntry.getKey();
            final Map<String, String> aerospikeProps = aSpikePropsEntry.getValue();
            boolean cacheDisabled=Boolean.valueOf(aerospikeProps.get(AEROSPIKE_PROP_DISABLED));
            final AeroSpikeOperationContainer opsContainer = new AeroSpikeOperationContainer();
            if (!cacheDisabled) {
                final String namespace = ModuleAwarePropertiesUtils.getRequiredPropertyFromSourceProps(
                        moduleName, AEROSPIKE_PROP_NAMESPACE, aerospikeProps, null);
                final String clusterNodes = ModuleAwarePropertiesUtils.getRequiredPropertyFromSourceProps(
                        moduleName, AEROSPIKE_PROP_CLUSTER_NODES, aerospikeProps, null);

                final Map<String, String> clientPolicyAttrs = ModuleAwarePropertiesUtils
                        .getRequiredSubPropertiesFromSourceProps(AEROSPIKE_POLICY_PROP_PREFIX, aerospikeProps);
                final String timeout = ModuleAwarePropertiesUtils.getRequiredPropertyFromSourceProps(
                        moduleName, AEROSPIKE_PROP_TIMEOUT, aerospikeProps, "300");

                final ClientPolicy clientPolicy = getClientPolicy(clientPolicyAttrs, timeout);
                final AerospikeClient aerospikeClient = getAerospikeClient(clusterNodes, clientPolicy);

                opsContainer.setClusterNodes(clusterNodes);
                opsContainer.setClientPolicy(clientPolicy);
                opsContainer.setAerospikeCacheOperations(getAppAwareAerospikeCommonOperations(aerospikeClient,namespace));
                CommonCacheContainer.register(moduleName+"-"+ CacheConstants.AEROSPIKE_CLIENT
                        ,getAppAwareAerospikeCommonOperations(aerospikeClient,namespace));
            } else {
                opsContainer.setAerospikeCacheOperations(new DummyCommonOperations<>());
            }

            appAwareCacheService.registerAppAwareCacheOperations(moduleName, opsContainer);

        }

        return appAwareCacheService;
    }

}
