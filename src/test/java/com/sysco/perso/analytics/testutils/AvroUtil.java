package com.sysco.perso.analytics.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AvroUtil {

  public static <T> ConsumerRecord<String, GenericRecord> getMockConsumerRecord(final T data) {
    ConsumerRecord<String, GenericRecord> consumerRecord = mock(ConsumerRecord.class);
    GenericRecord genericRecord = mock(GenericRecord.class);
    ObjectMapper om = new ObjectMapper();
    om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    String jsonString = "";
    try {
      jsonString = om.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    when(genericRecord.toString()).thenReturn(jsonString);
    when(consumerRecord.value()).thenReturn(genericRecord);
    return consumerRecord;
  }
}
