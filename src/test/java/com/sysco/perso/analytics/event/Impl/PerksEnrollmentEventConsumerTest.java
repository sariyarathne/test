package com.sysco.perso.analytics.event.Impl;

import com.sysco.perso.analytics.event.impl.PerksEnrollmentEventConsumer;
import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.payload.ChangeEventHeader;
import com.sysco.perso.analytics.event.payload.NewFields;
import com.sysco.perso.analytics.event.payload.OldFields;
import com.sysco.perso.analytics.service.PerksFacade;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@EnableAutoConfiguration
@SpringBootTest
public class PerksEnrollmentEventConsumerTest {

  @Autowired
  PerksEnrollmentEventConsumer perksEnrollmentEventConsumer;

  @MockBean
  PerksFacade perksFacade;

  @BeforeEach
  public void beforeEach() {
    Mockito.doNothing().when(perksFacade).enroll(ArgumentMatchers.any(AccountAttributeChangeEvent.class));
  }

  private AccountAttributeChangeEvent createAccountAttributeChangeEvent() {
    AccountAttributeChangeEvent event = new AccountAttributeChangeEvent();
    NewFields newFields = new NewFields();
    newFields.setAccountId("100-183956");
    newFields.setStatus("Active");
    newFields.setPerksEnrollmentDate("2022-06-30T00:00:00+00:00");
    event.setNewFields(newFields);

    return event;
  }

  @Test
  @Tag("accept")
  void whenAccept_thenSuccess() {
    GenericRecord genericRecord = mock(GenericRecord.class);
    ConsumerRecord consumerRecord = mock(ConsumerRecord.class);
    when(genericRecord.get("ChangeEventHeader")).thenReturn(new ChangeEventHeader());
    when(genericRecord.get("NewFields")).thenReturn(new NewFields());
    when(genericRecord.get("OldFields")).thenReturn(new OldFields());
    when(consumerRecord.value()).thenReturn(genericRecord);
    assertDoesNotThrow(() -> {
      this.perksEnrollmentEventConsumer.accept(consumerRecord);
    });
  }
}
