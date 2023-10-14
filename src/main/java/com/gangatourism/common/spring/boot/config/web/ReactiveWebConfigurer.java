package com.gangatourism.common.spring.boot.config.web;

import com.gangatourism.common.spring.boot.BeanName;
import com.gangatourism.common.spring.boot.config.CommonComponentsAutoConfiguration;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

/**
 * @author Rajveer Singh
 */
@Log4j2
@Configuration(value = BeanName.WEB_CONFIGURER_BEAN_NAME)
@AutoConfigureAfter(value = {CommonComponentsAutoConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(value = {HandlerResult.class, WebFluxConfigurer.class})
@ConditionalOnMissingBean(name = BeanName.WEB_CONFIGURER_BEAN_NAME)
@EnableWebFlux
public class ReactiveWebConfigurer implements WebFluxConfigurer {

  private final Environment env;

  private List<HandlerMethodArgumentResolver> customArgumentResolvers;

  public ReactiveWebConfigurer(
      final Environment env,
      final @Nullable List<HandlerMethodArgumentResolver> customArgumentResolvers) {
    this.env = env;
    this.customArgumentResolvers = customArgumentResolvers;
  }

  @PostConstruct
  public void onStartup() {
    if (this.env.getActiveProfiles().length != 0) {
      log.info(
          "Web application configuration, using profiles: {}",
          (Object[]) this.env.getActiveProfiles());
    }

    // App initialization and logging startup info
    AppInitializer.initialize(this.env, WebApplicationType.REACTIVE);

    log.info("Web application fully configured");
  }

  @Override
  public void configureArgumentResolvers(final ArgumentResolverConfigurer configurer) {
    // Any argument resolver defined as bean is already configured
    // Or Add custom argument resolvers explicitly
    if (CollectionUtils.isNotEmpty(this.customArgumentResolvers)) {
      configurer.addCustomResolver(
          this.customArgumentResolvers.stream().toArray(HandlerMethodArgumentResolver[]::new));
    }
  }

  // private final ObjectProvider<HandlerMethodArgumentResolver> argumentResolvers;
  // this.argumentResolvers.orderedStream().forEach(configurer::addCustomResolver);
}
