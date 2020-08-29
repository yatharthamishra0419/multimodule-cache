package com.multimodule.cache.annotation;

/*
 author : nitin.goyal
*/

import com.multimodule.cache.CacheConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiModuleCachable {

  String cacheName() default "";

  String cacheType() default CacheConstants.AEROSPIKE_CLIENT;

  long ttlInSeconds() default CacheConstants.CACHE_DEFAULT_TTL_SECONDS;

  TimeUnit timeUnit() default TimeUnit.SECONDS;

  Class serviceModuleNameProvider();

  String setName() default CacheConstants.DEFAULT_SET_NAME;

  String cacheKey() default "";



}
