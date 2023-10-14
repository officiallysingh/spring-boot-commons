package com.ksoot.common.spring.boot.config.security;

import com.ksoot.common.spring.boot.BeanName;
import com.ksoot.common.spring.boot.config.web.actuator.ActuatorEndpointProperties;
import com.ksoot.common.spring.boot.config.web.actuator.ActuatorUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

/**
 * @author Rajveer Singh
 */
@AutoConfiguration(value = BeanName.SECURITY_CONFIGURATION_BEAN_NAME)
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(prefix = "application.security", name = "enabled", havingValue = "true")
@ConditionalOnClass(WebSecurityConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
// @ConditionalOnMissingBean(name = SECURITY_CONFIGURATION_BEAN_NAME)
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity // Allow method annotations like @PreAuthorize
class ReactiveSecurityConfiguration {

  private final ServerAuthenticationEntryPoint authenticationEntryPoint;

  private final ServerAccessDeniedHandler accessDeniedHandler;

  private final ActuatorEndpointProperties actuatorEndpointProperties;

  private final SecurityProperties securityProperties;

  public ReactiveSecurityConfiguration(
      @Nullable final ServerAuthenticationEntryPoint authenticationEntryPoint,
      @Nullable final ServerAccessDeniedHandler accessDeniedHandler,
      @Nullable final ActuatorEndpointProperties actuatorEndpointProperties,
      final SecurityProperties securityProperties) {
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.accessDeniedHandler = accessDeniedHandler;
    this.actuatorEndpointProperties = actuatorEndpointProperties;
    this.securityProperties = securityProperties;
  }

  @Bean
  SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {
    // @formatter:off
    http.csrf()
        .disable()
        .authorizeExchange()
        .pathMatchers("/swagger-resources/**", "/swagger-ui/**", "/v2/api-docs", "/webjars/**")
        .permitAll()
        .pathMatchers(ActuatorUtils.getPaths(this.actuatorEndpointProperties))
        .permitAll()
        .pathMatchers(this.securityProperties.getUnsecuredUris())
        .permitAll()
        .anyExchange()
        .authenticated()
        .and()
        .oauth2ResourceServer()
        .jwt()
        .jwtAuthenticationConverter(this.jwtAuthenticationConverter())
        .jwtDecoder(new ReactiveJwtStringDecoder());

    if (this.authenticationEntryPoint != null) {
      http.exceptionHandling().authenticationEntryPoint(this.authenticationEntryPoint);
    }
    if (this.accessDeniedHandler != null) {
      http.exceptionHandling().accessDeniedHandler(this.accessDeniedHandler);
    }
    // @formatter:on
    return http.build();
  }

  private ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
    ReactiveJwtAuthenticationConverter jwtAuthenticationConverter =
        new ReactiveJwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
        new ReactiveJwtGrantedAuthoritiesConverterAdapter(
            JwtUtils.jwtGrantedAuthoritiesConverter()));
    // jwtAuthenticationConverter.setPrincipalClaimName(JwtUtils.PRINCIPLE_NAME_CLAIM_ID);
    return jwtAuthenticationConverter;
  }
}
