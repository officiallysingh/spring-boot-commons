package com.gangatourism.common.spring.boot.aws;

import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.aws.autoconfigure.context.properties.AwsRegionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(AwsRegionProperties.class)
public class AwsConfig {

  @Configuration
  @ConditionalOnClass(AmazonS3Client.class)
  public class AwsS3clientConfiguration {
    @Bean
    AwsS3client awsS3client(
        final AmazonS3Client amazonS3Client, final AwsRegionProperties awsRegionProperties) {
      return new AwsS3client(amazonS3Client, awsRegionProperties);
    }
  }
}
