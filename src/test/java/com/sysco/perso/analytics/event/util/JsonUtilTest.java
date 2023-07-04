package com.sysco.perso.analytics.event.util;

import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsonUtilTest {

  @Test
  public void whenParseOfferFulfillmentEvent_withIncorrectFormat() {
    GenericRecord genericRecord = mock(GenericRecord.class);
    when(genericRecord.toString()).thenReturn("{ test : test }");
    assertNull(JsonUtil.parseOfferFulfillmentEvent(genericRecord));
  }

  @Test
  public void whenParseAccountAttributeChangeEvent_withIncorrectFormat() {
    GenericRecord genericRecord = mock(GenericRecord.class);
    when(genericRecord.toString()).thenReturn("{ test : test }");
    assertNull(JsonUtil.parseAccountAttributeChangeEvent(genericRecord));
  }

}
