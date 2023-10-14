package com.ksoot.common.spring.boot;

import lombok.experimental.UtilityClass;
import org.springframework.context.support.AbstractApplicationContext;

/** Autoconfiguration BeanName names */
@UtilityClass
public final class BeanName {

  public static final String APPLICATION_TASK_EXECUTOR_BEAN_NAME = "applicationTaskExecutor";

  public static final String SCHEDULED_TASK_EXECUTOR_BEAN_NAME = "scheduledTaskExecutor";

  public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME =
      AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME;

  public static final String SECURITY_CONFIGURATION_BEAN_NAME = "securityConfiguration";

  public static final String WEB_CONFIGURER_BEAN_NAME = "webConfigurer";
}
