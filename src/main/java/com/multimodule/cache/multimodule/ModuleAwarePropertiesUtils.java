package com.multimodule.cache.multimodule;


import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ModuleAwarePropertiesUtils<T> {
	
	private static DefaultConversionService defaultConversionService = new DefaultConversionService();
	public static final String SINGLE_MODULE_NAME = "SingleModule";
    
	public static Map<String, Map<String, String>> readModuleWiseSubProperties(Environment environment, String keyPrefix) {
        final Map<String, Map<String, String>> moduleWiseSubProps = new HashMap<>();

        final ConfigurableEnvironment confEnv = (ConfigurableEnvironment)environment;
        
        for (PropertySource<?> propertySource : confEnv.getPropertySources()) {
            if(propertySource instanceof EnumerablePropertySource) {
				for (String name : ((EnumerablePropertySource<?>) propertySource).getPropertyNames()) {
					final int separatorIndex = name.indexOf('.');
					String propName;
					if (separatorIndex > 0) {
						String moduleName = null;
						if ((propName = name.substring(separatorIndex + 1)).startsWith(keyPrefix)) {
							moduleName = name.substring(0, separatorIndex);	
						} else if ((propName = name).startsWith(keyPrefix)) {
							moduleName = SINGLE_MODULE_NAME;
						} else {
							continue;
						}
						final String propNameWithoutPrefix = propName.substring(keyPrefix.length());
						moduleWiseSubProps.computeIfAbsent(moduleName, key -> new HashMap<>());
						moduleWiseSubProps.get(moduleName).put(propNameWithoutPrefix, confEnv.getProperty(name));
					}
				}
            }
        }

        return moduleWiseSubProps;
    }
  
    public static String getModuleName(ServiceModuleNameProvider moduleNameProvide) {
        return moduleNameProvide.getModuleName();
    }

	public static Map<String, String> getRequiredSubPropertiesFromSourceProps(String propertyPrefix, Map<String, String> sourceProps) {
		return (Map)sourceProps.entrySet().stream().filter((entry) -> {
			return ((String)entry.getKey()).startsWith(propertyPrefix);
		}).collect(Collectors.toMap((entry) -> {
			return ((String)entry.getKey()).substring(propertyPrefix.length());
		}, (entry) -> {
			return (String)entry.getValue();
		}));
	}

	public static <T> T getRequiredPropertyFromSourceProps(String moduleName, String propertyName, Map<String, T> sourceProps, T defaultValue) {
		T propValue;
		if (defaultValue == null) {
			propValue = sourceProps.get(propertyName);
		} else {
			T rawValue = sourceProps.containsKey(propertyName) ? sourceProps.get(propertyName) : defaultValue;
			propValue = (T) defaultConversionService.convert(rawValue, defaultValue.getClass());
		}

		if (propValue == null) {
			throw new IllegalStateException(String.format("No %s property defined in module: %s", propertyName, moduleName));
		} else {
			return propValue;
		}
	}
    
}
