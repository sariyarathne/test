package com.sysco.perso.analytics.event.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {
  private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

  public static AccountAttributeChangeEvent parseAccountAttributeChangeEvent(final GenericRecord genericRecord) {
    final String jsonString = genericRecord.toString();
    try {
      return new ObjectMapper().readValue(jsonString, AccountAttributeChangeEvent.class);
    } catch (JsonProcessingException e) {
      logger.error("Malformed AccountAttributeChangeEvent {}", e.getMessage());
    }
    return null;
  }

  public static OfferFulfillmentEvent parseOfferFulfillmentEvent(final GenericRecord genericRecord) {
    final String jsonString = genericRecord.toString();
    try {
      return new ObjectMapper().readValue(jsonString, OfferFulfillmentEvent.class);
    } catch (JsonProcessingException e) {
      logger.error("Malformed OfferFulfillmentEvent {}", e.getMessage());
    }
    return null;
  }
}
