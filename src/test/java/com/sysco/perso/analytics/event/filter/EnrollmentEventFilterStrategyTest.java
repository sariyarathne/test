package com.sysco.perso.analytics.event.filter;

import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.payload.ChangeEventHeader;
import com.sysco.perso.analytics.event.payload.NewFields;
import com.sysco.perso.analytics.event.payload.OldFields;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@EnableAutoConfiguration
@SpringBootTest
public class EnrollmentEventFilterStrategyTest {

  private ConsumerRecord createEvent(String accountId, String status, String perksEnrollmentDate
          , String syscoPerk, String changeTyoe) {

    AccountAttributeChangeEvent event = new AccountAttributeChangeEvent();
    NewFields newFields = new NewFields();
    newFields.setAccountId(accountId);
    newFields.setStatus(status);
    newFields.setPerksEnrollmentDate(perksEnrollmentDate);
    newFields.setSyscoPerks(syscoPerk);
    ChangeEventHeader changeEventHeader = new ChangeEventHeader();
    changeEventHeader.setEntityName("entity1");
    changeEventHeader.setChangeType(changeTyoe);
    changeEventHeader.setChangedFields(Collections.emptyList());
    changeEventHeader.setRecordIds(Collections.emptyList());
    event.setNewFields(newFields);
    event.setChangeEventHeader(changeEventHeader);

    GenericRecord genericRecord = mock(GenericRecord.class);
    ConsumerRecord consumerRecord = mock(ConsumerRecord.class);


    when(genericRecord.get("ChangeEventHeader")).thenReturn(changeEventHeader);
    when(genericRecord.get("NewFields")).thenReturn(newFields);
    when(genericRecord.get("OldFields")).thenReturn(new OldFields());
    when(consumerRecord.value()).thenReturn(genericRecord);
    return consumerRecord;
  }

  @Autowired
  RecordFilterStrategy recordFilterStrategy;

  @Test
  @Tag("enrollmentEventFilterFalse")
  void whenEnrollmentEvenFilterFalse_thenSuccess() {
    ConsumerRecord event = createEvent("100-183956", "Active", "2022-06-30T00:00:00+00:00",
            "Enrolled", null);
    assertTrue(recordFilterStrategy.filter(event));
  }

  @Test
  @Tag("perksEligibleOpcoFilterFalse")
  void whenPerksEligibleOpcoFalse_thenSuccess() {
    ConsumerRecord event = createEvent("100-183956", "Active", "2022-06-30T00:00:00+00:00",
            "Enrolled", "scheduled:Perks_Enrollment_Date__c");
    assertTrue(recordFilterStrategy.filter(event));
  }

  @Test
  @Tag("CustomerActiveFalse")
  void whenCustomerActiveFalse_thenSuccess() {
    ConsumerRecord event = createEvent("123-183956", "Inactive", "2022-06-30T00:00:00+00:00",
            "Enrolled", "scheduled:Perks_Enrollment_Date__c");
    assertTrue(recordFilterStrategy.filter(event));
  }

}
