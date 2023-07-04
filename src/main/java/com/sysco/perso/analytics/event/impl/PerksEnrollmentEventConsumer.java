package com.sysco.perso.analytics.event.impl;

import com.sysco.perso.analytics.event.EventConsumer;
import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.util.JsonUtil;
import com.sysco.perso.analytics.service.PerksFacade;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PerksEnrollmentEventConsumer implements EventConsumer {
  private final Logger logger = LoggerFactory.getLogger(PerksEnrollmentEventConsumer.class);
  private static final String TOPIC = "account.fct.attributes.0";
  private final PerksFacade perksFacade;

  @Autowired
  public PerksEnrollmentEventConsumer(final PerksFacade perksFacade) {
    this.perksFacade = perksFacade;
  }

  @KafkaListener(topics = TOPIC, id = "account-attribute-listener",
          containerFactory = "perksEnrollmentEventListenerContainerFactory")
  public void accept(final ConsumerRecord<String, GenericRecord> consumerRecord) {
    final AccountAttributeChangeEvent event = JsonUtil.parseAccountAttributeChangeEvent(consumerRecord.value());
    logger.info("AccountAttributeChangeEvent received {}", consumerRecord.value());
    if (Objects.nonNull(event)) {
      perksFacade.enroll(event);
    }
  }
}
