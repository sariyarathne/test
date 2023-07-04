package com.sysco.perso.analytics.event;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface EventConsumer {
  void accept(ConsumerRecord<String, GenericRecord> event);
}
