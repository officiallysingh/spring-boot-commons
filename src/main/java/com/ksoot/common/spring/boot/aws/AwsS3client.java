package com.ksoot.common.spring.boot.aws;

import static org.apache.commons.io.IOUtils.DIR_SEPARATOR_UNIX;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.ksoot.common.spring.util.FilePartUtils;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.io.FilenameUtils;
import org.springframework.cloud.aws.autoconfigure.context.properties.AwsRegionProperties;
import org.springframework.http.codec.multipart.FilePart;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

public class AwsS3client {

  private final AmazonS3Client amazonS3Client;

  private final S3Utilities s3Utilities;

  public AwsS3client(
      final AmazonS3Client amazonS3Client, final AwsRegionProperties awsRegionProperties) {
    this.amazonS3Client = amazonS3Client;
    this.s3Utilities =
        S3Utilities.builder().region(Region.of(awsRegionProperties.getStatic())).build();
  }

  public URL uploadFile(final String bucketName, final FilePart file, final String key) {
    InputStream inputStream = FilePartUtils.getInputStream(file);
    this.amazonS3Client.putObject(bucketName, key, inputStream, null);
    GetUrlRequest urlReq = GetUrlRequest.builder().bucket(bucketName).key(key).build();
    return this.s3Utilities.getUrl(urlReq);
  }

  public URL uploadFile(final String bucketName, final FilePart file) {
    return uploadFile(bucketName, file, FilenameUtils.getName(file.filename()));
  }

  public URL uploadFileToFolder(final String bucketName, final FilePart file, final String folder) {
    String key = folder + DIR_SEPARATOR_UNIX + FilenameUtils.getName(file.filename());
    return uploadFile(bucketName, file, key);
  }

  public URL getURI(final String bucketName, final String key) {
    GetUrlRequest urlReq = GetUrlRequest.builder().bucket(bucketName).key(key).build();
    return this.s3Utilities.getUrl(urlReq);
  }

  public List<URL> getURIs(
      final String bucketName, final String prefix, final Predicate<String> filter) {
    return this.amazonS3Client.listObjectsV2(bucketName, prefix).getObjectSummaries().stream()
        .map(S3ObjectSummary::getKey)
        .filter(filter)
        .map(
            key -> {
              GetUrlRequest urlReq = GetUrlRequest.builder().bucket(bucketName).key(key).build();
              return this.s3Utilities.getUrl(urlReq);
            })
        .toList();
  }
}
