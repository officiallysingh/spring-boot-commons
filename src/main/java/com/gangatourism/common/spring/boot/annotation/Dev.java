package com.gangatourism.common.spring.boot.annotation;

import com.gangatourism.common.spring.boot.config.SpringProfiles;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Profile;

/**
 * @author Rajveer Singh
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Profile(SpringProfiles.DEVELOPMENT)
public @interface Dev {}
