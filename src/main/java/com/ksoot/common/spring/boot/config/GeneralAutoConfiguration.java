package com.ksoot.common.spring.boot.config;

import com.ksoot.common.spring.boot.BeanName;
import com.ksoot.common.spring.boot.pagination.PaginatedResourceAssembler;
import com.ksoot.common.spring.util.MessageProvider;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;

@AutoConfiguration
@EnableConfigurationProperties(
    value = {TaskExecutionProperties.class, TaskSchedulingProperties.class})
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class GeneralAutoConfiguration {

  @ConditionalOnMissingBean(value = ApplicationEventMulticaster.class)
  @Bean
  ApplicationEventMulticaster applicationEventMulticaster(
      @Qualifier(BeanName.APPLICATION_TASK_EXECUTOR_BEAN_NAME) final Executor taskExecutor) {
    SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
    eventMulticaster.setTaskExecutor(taskExecutor);
    return eventMulticaster;
  }

  @ConditionalOnMissingBean(value = MessageProvider.class)
  @Bean
  MessageProvider messageProvider(final MessageSource messageSource) {
    return new MessageProvider(messageSource);
  }

  @ConditionalOnMissingBean(value = SpringProfiles.class)
  @Bean
  SpringProfiles springProfiles(final Environment environment) {
    return new SpringProfiles(environment);
  }

  @ConditionalOnMissingBean(value = PaginatedResourceAssembler.class)
  @Bean
  PaginatedResourceAssembler paginatedResourceAssembler(
      @Nullable final HateoasPageableHandlerMethodArgumentResolver resolver) {
    return new PaginatedResourceAssembler(resolver);
  }

  @ConditionalOnMissingBean(value = DefaultBeanRegistry.class)
  @Bean
  BeanRegistry defaultBeanRegistry(final ApplicationContext applicationContext) {
    BeanRegistry beanRegistry = new DefaultBeanRegistry();
    beanRegistry.setApplicationContext(applicationContext);
    return beanRegistry;
  }

  // @ConditionalOnMissingBean(value = Validator.class)
  // @Bean
  // Validator validator() {
  // return Validation.buildDefaultValidatorFactory().getValidator();
  // }
}
