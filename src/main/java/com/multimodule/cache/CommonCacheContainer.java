package com.multimodule.cache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration("com.multimodule.cache.CommonCacheContainer")
public class CommonCacheContainer implements BeanFactoryAware {

    private static Map<String, IAppAwareCommonCacheOperations> moduleToCommonCacheOperationsMap=
            new ConcurrentHashMap<>();

    private static ConfigurableBeanFactory beanFactoryStatic;

    public static void register(String moduleNameAndProvider,
                         IAppAwareCommonCacheOperations iAppAwareCommonCacheOperations){
        beanFactoryStatic.registerSingleton(moduleNameAndProvider, iAppAwareCommonCacheOperations);
        moduleToCommonCacheOperationsMap.put(moduleNameAndProvider, iAppAwareCommonCacheOperations);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        beanFactoryStatic=(ConfigurableBeanFactory) beanFactory;
    }
}
