package com.multimodule.cache;

import com.multimodule.cache.multimodule.ServiceModuleNameProvider;
import org.springframework.stereotype.Component;

/**
 * This class is used to get module name (maven artifactId) injected and use that to interact with
 * properties and libraries. This comes handy to ensure that we have module level segregation even
 * if they are deployed with other modules and help us take these out as new service if required.
 *
 * Location of this class in respect to module is very significant.
 *
 * E.g. if this class is present in module with artifactId 'module-one', it will return name as
 * 'module-one'. If you move it to 'module-two', it will start returning 'module-two' as this is
 * resolved at build time.
 */
@Component("com.ie.naukri.core.cache.TestModuleNameProvider")
public class TestModuleNameProvider implements ServiceModuleNameProvider {

  private String moduleName = "module1";

  @Override
  public String getModuleName() {
    return moduleName;
  }
}