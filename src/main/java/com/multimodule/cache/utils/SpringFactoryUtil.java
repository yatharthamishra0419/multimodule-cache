package com.multimodule.cache.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * A factory for those classes that need to have static methods and those static methods need access
 * to spring beans
 *
 * @author nitin.goyal
 */
@Component
public class SpringFactoryUtil implements ApplicationContextAware {

  private static ApplicationContext myContext;

  public static boolean containsBean(String beanName) {
    return myContext.containsBean(beanName);
  }

  public static Object getBean(String beanName) {
    return myContext.getBean(beanName);
  }

  public static Object getBean(Class beanclass) {
    return myContext.getBean(beanclass);
  }

  public static Object getBean(String beanName, Object... obj) {
    return myContext.getBean(beanName, obj);
  }

  public static Object getBean(String beanName, boolean required) {
    if (!required && !myContext.containsBean(beanName)) {
      return null;
    }
    return myContext.getBean(beanName);
  }

  public static ApplicationContext getApplicationContext() {
    return myContext;
  }

  @Override
  public void setApplicationContext(ApplicationContext context)
      throws BeansException {
    myContext = context;
  }


}

