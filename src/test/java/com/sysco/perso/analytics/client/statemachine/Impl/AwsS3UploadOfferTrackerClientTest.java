package com.sysco.perso.analytics.client.statemachine.Impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.sysco.perso.analytics.client.statemachine.OfferTrackerClient;
import com.sysco.perso.analytics.client.statemachine.data.TrackerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableAutoConfiguration
@SpringBootTest
public class AwsS3UploadOfferTrackerClientTest {

  @MockBean
  AmazonS3 s3Client;

  @Autowired
  OfferTrackerClient offerTrackerClient;

  @BeforeEach
  public void beforeEach() {
    Mockito.when(this.s3Client.putObject(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
            ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(
                    ObjectMetadata.class))).thenReturn(new PutObjectResult());
  }

  private TrackerData createTrackerData() {
    return TrackerData.builder().customerNumber("183956").drawdownPeriod(6).offerEndDate(LocalDate.parse("2016-10-16"))
            .offerStartDate(LocalDate.parse("2016-08-16")).offerSource("pfw").hurdleLevel(10000).opcoId("100")
            .rewardValue(894).sampleGroup("test").build();
  }

  @Test
  @Tag("uploadOfferTracker")
  void uploadOfferTracker_thenSuccess() {
    List<TrackerData> expectedTrackerData = Collections.singletonList(createTrackerData());
    boolean trackerUploadResult = this.offerTrackerClient.startTracker(expectedTrackerData);

    assertTrue(trackerUploadResult);
  }

  @Test
  @Tag("uploadOfferTracker_Fail")
  void uploadOfferTracker_thenFail() {
    Mockito.when(this.s3Client.putObject(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
            ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(
                    ObjectMetadata.class))).thenThrow(SdkClientException.class);

    List<TrackerData> expectedTrackerData = Collections.singletonList(createTrackerData());
    boolean trackerUploadResult = this.offerTrackerClient.startTracker(expectedTrackerData);

    assertFalse(trackerUploadResult);
  }
}
