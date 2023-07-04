package com.sysco.perso.analytics.event.filter;

import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.payload.PerksEnrollType;
import com.sysco.perso.analytics.event.util.JsonUtil;
import com.sysco.perso.analytics.util.CustomerInfoUtil;
import lombok.NonNull;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

import java.util.Arrays;
import java.util.Objects;

public class EnrollmentEventFilterStrategy implements RecordFilterStrategy<String, GenericRecord> {
  private static final String PERKS_TYPE = "scheduled:Perks_Enrollment_Date__c";

  private static final String ACTIVE = "Active";

  @Value("${perks.eligible.opco.list}")
  private String opcoList;

  @Override
  public boolean filter(@NonNull ConsumerRecord<String, GenericRecord> consumerRecord) {
    final AccountAttributeChangeEvent event = JsonUtil.parseAccountAttributeChangeEvent(consumerRecord.value());
    return Objects.isNull(event) || !isPerksEnrollmentEvent(event) || !isPerksEligibleOpco(event) ||
            !isCustomerActive(event);
  }

  private boolean isPerksEnrollmentEvent(AccountAttributeChangeEvent event) {
    return PerksEnrollType.getValues().contains(event.getNewFields().getSyscoPerks()) &&
            PERKS_TYPE.equals(event.getChangeEventHeader().getChangeType());
  }

  private boolean isCustomerActive(AccountAttributeChangeEvent event) {
    return ACTIVE.equals(event.getNewFields().getStatus());
  }

  private boolean isPerksEligibleOpco(AccountAttributeChangeEvent event) {
    final String opcoId =
            CustomerInfoUtil.splitToOpcoAndCustomerId(event.getNewFields().getAccountId())[0];
    return opcoList.isEmpty() || Arrays.asList(opcoList.split(",")).contains(opcoId);
  }
}
