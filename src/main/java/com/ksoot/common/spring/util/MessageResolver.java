package com.ksoot.common.spring.util;

/**
 * @author Rajveer Singh
 */
public interface MessageResolver {

  public String messageCode();

  public default String defaultMessage() {
    return "Message not found for code: '" + this.messageCode() + "'";
  }
}
