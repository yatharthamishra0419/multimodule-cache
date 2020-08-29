package com.multimodule.cache.annotation;/*
 author : nitin.goyal
*/

import com.multimodule.cache.aerospike.cacheOperations.AppAwareAeroSpikeCommonOperations;
import com.multimodule.cache.CacheConstants;
import com.multimodule.cache.IAppAwareCommonCacheOperations;
import com.multimodule.cache.cacheconfig.AppAwareCacheConfig;
import com.multimodule.cache.cacheconfig.CacheConfig;
import com.multimodule.cache.multimodule.ServiceModuleNameProvider;
import com.multimodule.cache.utils.SpringFactoryUtil;
import com.multimodule.cache.utils.GeneralUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@EnableAspectJAutoProxy
public class AnnotationBasedCacheHandler {

  Logger logger ;

  public AnnotationBasedCacheHandler(){
    logger= LoggerFactory.getLogger(AnnotationBasedCacheHandler.class);
  }

  @Autowired
  private AppAwareCacheConfig appAwareCacheConfig;

  @Around("@annotation(MultiModuleCachable)")
  public Object handleCaching(ProceedingJoinPoint joinPoint)
      throws Throwable {
    Object[] args = ((MethodInvocationProceedingJoinPoint) joinPoint).getArgs();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    MultiModuleCachable multiModuleCachable = method.getAnnotation(MultiModuleCachable.class);
    if (GeneralUtils.isNullOrEmpty(args) && GeneralUtils.isNullOrEmpty(multiModuleCachable.cacheKey())) {
      logger.error("Method " + ((MethodInvocationProceedingJoinPoint) joinPoint).getSignature()
          + " Does not have any arguments.So not looking up into cache.Also no key specified");
      return null;
    }


    String moduleName=getModuleName(multiModuleCachable);
    String cacheName = multiModuleCachable.cacheName();
    String cacheSource= multiModuleCachable.cacheType();
    IAppAwareCommonCacheOperations iAppAwareCommonCacheOperations =null;
    try {
      iAppAwareCommonCacheOperations = (IAppAwareCommonCacheOperations)
              SpringFactoryUtil.getBean(moduleName +
                      "-"
                      + cacheSource);
    }catch (Exception e){
      throw new IllegalArgumentException(cacheSource + " properties not configured in "+ moduleName);
    }
    CacheConfig cacheConfig = getCacheConfig(moduleName, cacheName);
    String setName=getSetName(multiModuleCachable,cacheConfig);
    String key= getKey(cacheConfig, iAppAwareCommonCacheOperations,
            setName, multiModuleCachable,args);
    Object output = iAppAwareCommonCacheOperations.get(key);
    if (output != null) {
      return output;
    }
    long ttl=getTtl(multiModuleCachable,cacheConfig);
    Object response = joinPoint.proceed();
    iAppAwareCommonCacheOperations.set(key, response, ttl,
            getTimeUnit(multiModuleCachable,cacheConfig));
    return response;
  }
  private TimeUnit getTimeUnit(MultiModuleCachable multiModuleCachable, CacheConfig cacheConfig){
    TimeUnit timeUnit= multiModuleCachable.timeUnit();
    if(multiModuleCachable.timeUnit().equals(TimeUnit.SECONDS) &&
            !GeneralUtils.isNullOrEmpty(cacheConfig.getTimeUnit())){
      timeUnit=cacheConfig.getTimeUnit();
    }
    return timeUnit;
  }

  private String getSetName(MultiModuleCachable multiModuleCachable, CacheConfig cacheConfig){
    String setName= multiModuleCachable.setName();
    if(multiModuleCachable.setName().equals(CacheConstants.DEFAULT_SET_NAME) &&
            !GeneralUtils.isNullOrEmpty(cacheConfig.getSetName())){
      setName=cacheConfig.getSetName();
    }
    return setName;
  }

  private long getTtl(MultiModuleCachable multiModuleCachable, CacheConfig cacheConfig){
    long ttl= multiModuleCachable.ttlInSeconds();
    if(multiModuleCachable.ttlInSeconds() == CacheConstants.CACHE_DEFAULT_TTL_SECONDS
            && !GeneralUtils.isNullOrEmpty(cacheConfig.getTtl())){
      ttl=cacheConfig.getTtl();
    }
    return ttl;
  }

  private String getModuleName(MultiModuleCachable multiModuleCachable){
    Component component = (Component) multiModuleCachable.serviceModuleNameProvider().
            getAnnotation(Component.class);
    ServiceModuleNameProvider serviceModuleNameProvider=(ServiceModuleNameProvider)
            SpringFactoryUtil.getBean(component.value());
    return serviceModuleNameProvider.getModuleName();
  }

  private String getKey(CacheConfig cacheConfig,
                        IAppAwareCommonCacheOperations iAppAwareCommonCacheOperations,
                        String setName, MultiModuleCachable multiModuleCachable,
                        Object[] args ){
    String key= multiModuleCachable.cacheKey();
    if(GeneralUtils.isNullOrEmpty(key)){
      for (int counter = 0; counter < args.length; counter++) {
        key += args[counter].toString();
      }
    }else {
      try{
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(key);
      key=(String) expression.getValue(args);
      }catch (Exception e){

      }
    }
    String updatedKey=cacheConfig.getPrefix()+key;
    if(iAppAwareCommonCacheOperations instanceof AppAwareAeroSpikeCommonOperations)
      updatedKey=setName+CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR+updatedKey;
    return updatedKey;
  }

  private CacheConfig getCacheConfig(String moduleName,String cacheName){
    if(GeneralUtils.isNullOrEmpty(cacheName)
            || GeneralUtils.isNullOrEmpty(
                    appAwareCacheConfig.getCacheConfig(moduleName+
                            "-"
                            +cacheName) ))
        return new CacheConfig();
    return appAwareCacheConfig.getCacheConfig(moduleName+
            "-"
            +cacheName);
  }

}
