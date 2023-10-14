package com.gangatourism.common.spring.boot.config.security;

import com.gangatourism.common.spring.boot.BeanName;
import com.gangatourism.common.spring.boot.config.web.actuator.ActuatorEndpointProperties;
import com.gangatourism.common.spring.boot.config.web.actuator.ActuatorUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * @author Rajveer Singh
 */
@AutoConfiguration(value = BeanName.SECURITY_CONFIGURATION_BEAN_NAME)
@EnableConfigurationProperties(value = {SecurityProperties.class, ActuatorEndpointProperties.class})
@ConditionalOnProperty(prefix = "application.security", name = "enabled", havingValue = "true")
@ConditionalOnClass(
    value = {WebSecurityConfiguration.class, JwtAuthenticationConverter.class, JwtDecoder.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
// @ConditionalOnMissingBean(name = SECURITY_CONFIGURATION_BEAN_NAME)
@AutoConfigureBefore(SecurityAutoConfiguration.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true) // Allow method annotations like
// @PreAuthorize, not required as of now
class SecurityConfiguration {

  private final AuthenticationEntryPoint authenticationEntryPoint;

  private final AccessDeniedHandler accessDeniedHandler;

  private final ActuatorEndpointProperties actuatorEndpointProperties;

  private final SecurityProperties securityProperties;

  public SecurityConfiguration(
      @Nullable final AuthenticationEntryPoint authenticationEntryPoint,
      @Nullable final AccessDeniedHandler accessDeniedHandler,
      @Nullable final ActuatorEndpointProperties actuatorEndpointProperties,
      final SecurityProperties securityProperties) {
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.accessDeniedHandler = accessDeniedHandler;
    this.actuatorEndpointProperties = actuatorEndpointProperties;
    this.securityProperties = securityProperties;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    http.csrf()
        .disable()
        .authorizeHttpRequests(
            (requests) ->
                requests
                    .requestMatchers(
                        "/swagger-resources/**", "/swagger-ui/**", "/v2/api-docs", "/webjars/**")
                    .permitAll()
                    .requestMatchers(
                        //						this.securityProperties.isBypassActuators()
                        //						?
                        ActuatorUtils.getPaths(this.actuatorEndpointProperties)
                        //						: RequestMatcher.MatchResult.notMatch()
                        )
                    .permitAll()
                    .requestMatchers(this.securityProperties.getUnsecuredUris())
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer()
        .jwt()
        .jwtAuthenticationConverter(this.jwtAuthenticationConverter())
        .decoder(new JwtStringDecoder());

    if (this.authenticationEntryPoint != null) {
      http.exceptionHandling().authenticationEntryPoint(this.authenticationEntryPoint);
    }
    if (this.accessDeniedHandler != null) {
      http.exceptionHandling().accessDeniedHandler(this.accessDeniedHandler);
    }
    // @formatter:on
    return http.build();
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
        JwtUtils.jwtGrantedAuthoritiesConverter());
    jwtAuthenticationConverter.setPrincipalClaimName(JwtUtils.PRINCIPLE_NAME_CLAIM_ID);
    return jwtAuthenticationConverter;
  }
}
