package com.sysco.perso.analytics.client.statemachine.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.StringInputStream;
import com.sysco.perso.analytics.client.statemachine.OfferTrackerClient;
import com.sysco.perso.analytics.client.statemachine.data.TrackerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class AwsS3UploadOfferTrackerClient implements OfferTrackerClient {
  @Value("${dtc.offers.s3.bucket.key}")
  private String s3OfferBucketBaseKey;

  @Value("${dtc.offers.s3.bucket.name}")
  private String s3OfferBucketName;

  private static final Logger logger = LoggerFactory.getLogger(AwsS3UploadOfferTrackerClient.class);

  private final AmazonS3 s3Client;

  public AwsS3UploadOfferTrackerClient(final AmazonS3 s3Client) {
    this.s3Client = s3Client;
  }

  @Override
  public boolean startTracker(final List<TrackerData> objects) {
    final String headers =
            "co_skey,cust_nbr,offer_name,hurdle_level,frequency,offer_start_date,offer_end_date,reward_value," +
                    "sample_group,offer_source,drawdown_period\n";
    if (!objects.isEmpty()) {
      InputStream inputStream = null;
      final StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(headers);
      objects.stream().map(this::toCsvString).forEach(stringBuilder::append);
      try {
        inputStream = new StringInputStream(stringBuilder.toString());
      } catch (UnsupportedEncodingException e) {
        logger.error("Offer file encoding error : {}", e.getMessage());
      }
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(stringBuilder.toString().getBytes(StandardCharsets.UTF_8).length);
      final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      final String runDate = LocalDate.now().toString();
      final String key = s3OfferBucketBaseKey + "/" + runDate + "/perks_membership_" + timestamp.getTime() + ".csv";
      try {
        this.s3Client.putObject(s3OfferBucketName, key, inputStream, metadata);
      } catch (SdkClientException e) {
        logger.error("Could not upload to S3 bucket {}", e.getMessage());
        return false;
      }
      logger.info("Offer file uploaded to sysco-bcg-personalization/{}", key);
    }
    return true;
  }

  private String toCsvString(final TrackerData trackerData) {
    return String.join(",", trackerData.getOpcoId(), trackerData.getCustomerNumber(), trackerData.getOfferName(),
            trackerData.getHurdleLevel().toString(), trackerData.getFrequency(),
            trackerData.getOfferStartDate().toString(),
            trackerData.getOfferEndDate().toString(), trackerData.getRewardValue().toString(),
            trackerData.getSampleGroup(), trackerData.getOfferSource(),
            trackerData.getDrawdownPeriod().toString()) + "\n";
  }
}
