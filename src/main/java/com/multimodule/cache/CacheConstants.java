package com.multimodule.cache;

/**
 * A constants class to hold cache keys
 *
 * @author nitin.goyal
 */
public interface CacheConstants {

  long CACHE_DEFAULT_TTL_SECONDS=1800l;

  String TTL_SUFFIX = "cache.ttl.in.sec";
  String GET_TIMEOUT_SUFFIX = "cache.get.timeout.in.milliseconds";
  String SET_TIMEOUT_SUFFIX = "cache.set.timeout.in.milliseconds";
  String ENDPOINT_SUFFIX = "endpoint";
  String MASTER = "master";
  String PASSWORD_SUFFIX = "password";
  String MAX_TOTAL_CONNECTION_SUFFIX = "max.total.connection";
  String MIN_IDLE_CONNECTION_SUFFIX = "min.idle.connection";
  String DEFAULT_TIMEOUT_MILLIS_SUFFIX = "default.timeout.in.milliseconds";
  String MAX_WAIT_IN_MILLIS_SUFFIX = "max.wait.in.milliseconds";
  String TURN_OFF_CACHE_SUFFIX = "turn.off";

  String AEROSPIKE_CLIENT = "aerospike";
  String JVM_CLIENT = "jvm";
  String REDIS_CLIENT = "redis";
  String DEFAULT_CLIENT = "DEFAULT";
  String DEFAULT_SET_NAME="ngSet";
  String AEROSPIKE_SET_KEY_SEPERATOR="=";

  String DEFAULT_CACHE_NAME = "default";

  String AEROSPIKE_NAMESPACE = "ngnamespace";
  String AEROSPIKE_SET = "ngset";
  String AEROSPIKE_DEFAULT_BIN = "ngbin";

}
