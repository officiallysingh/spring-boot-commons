package com.gangatourism.common.spring.boot.config.security;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Rajveer Singh
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@ConditionalOnProperty(prefix = "application.security", name = "enabled", havingValue = "true")
@ConfigurationProperties(prefix = "application.security")
@Valid
public class SecurityProperties {

  private boolean enabled = false;

  private boolean bypassActuators = true;

  private String[] unsecuredUris = {};
}
