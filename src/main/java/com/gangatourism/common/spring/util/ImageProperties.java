package com.gangatourism.common.spring.util;

import jakarta.validation.Valid;
import java.util.Arrays;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@Getter
@Setter
@NoArgsConstructor
@ToString
@ConfigurationProperties(prefix = "application.images")
@Valid
public class ImageProperties {

  public static final String ALL_VALUE = "*";

  public static final String[] ALL_TYPES = {"gif", "png", "jpg", "jpeg"};

  private String bucketName;

  private String[] allowedExtensions = ALL_TYPES;

  public void setAllowedExtensions(final String[] allowedExtensions) {
    if (ArrayUtils.isNotEmpty(allowedExtensions)
        && Arrays.asList(allowedExtensions).contains(ALL_VALUE)) {
      this.allowedExtensions = ALL_TYPES;
    } else {
      this.allowedExtensions = allowedExtensions;
    }
  }

  public boolean isAllowed(final String extension) {
    return Arrays.asList(allowedExtensions).contains(extension);
  }

  public String allowedExtensionsString() {
    return String.join(",", Arrays.asList(this.allowedExtensions));
  }

  public static void main(String[] args) {
    System.out.println("100MB = " + DataSize.ofMegabytes(100));
    System.out.println("20KB = " + DataSize.ofKilobytes(20));
  }
}
