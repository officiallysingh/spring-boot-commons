package com.ksoot.common.spring.boot;

import com.ksoot.common.ConfigException;
import com.ksoot.common.spring.util.ExternalFileLoaderUtil;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

/**
 * @author Rajveer Singh
 */
@Log4j2
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultPropertiesEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private static final String DEFAULT_PROPERTIES = "config/defaults.yml";

  private static final String DEFAULT_PROPERTIES_SOURCE_NAME = "defaultProperties";

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    log.debug("Adding default properties from : " + DEFAULT_PROPERTIES);

    Properties defaultProperties = new Properties();
    try {
      Resource extFile = new ClassPathResource(DEFAULT_PROPERTIES);
      if (extFile.exists()) {
        defaultProperties.putAll(ExternalFileLoaderUtil.loadProperties(extFile));
        log.info("Default property file: " + DEFAULT_PROPERTIES + " added in property sources");
      } else {
        throw new ConfigException("File not found: " + DEFAULT_PROPERTIES);
      }
    } catch (IOException e) {
      throw new ConfigException(
          "Exception while reading default properties file: " + DEFAULT_PROPERTIES, e);
    }

    if (!defaultProperties.isEmpty()) {
      MutablePropertySources propertySources = environment.getPropertySources();
      PropertySource<?> existingDefaultProperties =
          propertySources.remove(DEFAULT_PROPERTIES_SOURCE_NAME);
      if (existingDefaultProperties != null) {
        Object src = existingDefaultProperties.getSource();
        if (ClassUtils.isAssignableValue(Map.class, src)) {
          defaultProperties.putAll((Map<?, ?>) src);
        } else {
          log.error(
              "Unknown default property source type: " + src.getClass() + ", handle accordingly");
        }
      } else {
        log.info("No default properties found before adding external default properties");
      }
      // application.setDefaultProperties(defaultProperties);
      propertySources.addLast(
          new PropertiesPropertySource(DEFAULT_PROPERTIES_SOURCE_NAME, defaultProperties));
    } else {
      log.debug("No external default properties defined");
    }
  }
}
