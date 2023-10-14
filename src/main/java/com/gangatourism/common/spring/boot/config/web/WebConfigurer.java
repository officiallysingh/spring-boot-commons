package com.gangatourism.common.spring.boot.config.web;

import com.gangatourism.common.spring.boot.BeanName;
import com.gangatourism.common.spring.boot.config.CommonComponentsAutoConfiguration;
import com.gangatourism.common.spring.boot.config.web.actuator.ActuatorEndpointProperties;
import com.gangatourism.common.spring.boot.config.web.actuator.ActuatorUtils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 *
 * @author Rajveer Singh
 */
@Log4j2
@Configuration(value = BeanName.WEB_CONFIGURER_BEAN_NAME)
@AutoConfigureAfter(value = {CommonComponentsAutoConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
// @ConditionalOnMissingBean(name = WEB_CONFIGURER_BEAN_NAME)
public class WebConfigurer implements ServletContextInitializer, WebMvcConfigurer {

  private final Environment env;

  private final ActuatorEndpointProperties actuatorEndpointProperties;

  private List<HandlerMethodArgumentResolver> customArgumentResolvers;

  private List<HandlerInterceptor> customInterceptors;

  public WebConfigurer(
      final Environment env,
      @Nullable final ActuatorEndpointProperties actuatorEndpointProperties,
      @Nullable final List<HandlerMethodArgumentResolver> customArgumentResolvers,
      @Nullable final List<HandlerInterceptor> customInterceptors) {
    this.env = env;
    this.actuatorEndpointProperties = actuatorEndpointProperties;
    this.customArgumentResolvers = customArgumentResolvers;
    this.customInterceptors = customInterceptors;
  }

  @Override
  public void onStartup(final ServletContext servletContext) throws ServletException {
    if (this.env.getActiveProfiles().length != 0) {
      log.info(
          "Web application configuration, using profiles: {}",
          (Object[]) this.env.getActiveProfiles());
    }

    // App initialization and logging startup info
    AppInitializer.initialize(this.env, WebApplicationType.SERVLET);

    log.info("Web application fully configured");
  }

  @Override
  public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
    if (!CollectionUtils.isEmpty(this.customArgumentResolvers)) {
      this.customArgumentResolvers.forEach(argumentResolvers::add);
    }
    // Add any custom method argument resolvers
  }

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    if (CollectionUtils.isNotEmpty(this.customInterceptors)) {
      for (HandlerInterceptor interceptor : this.customInterceptors) {
        registry
            .addInterceptor(interceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(ActuatorUtils.getPaths(this.actuatorEndpointProperties));
      }
    }
  }

  // @Override
  // public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
  // configurer.defaultContentType(MediaType.APPLICATION_JSON);
  // }
}
