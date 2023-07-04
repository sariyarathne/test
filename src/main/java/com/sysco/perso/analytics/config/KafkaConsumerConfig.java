package com.sysco.perso.analytics.config;

import com.sysco.perso.analytics.event.filter.EnrollmentEventFilterStrategy;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
  @Autowired
  private KafkaProperties kafkaProperties;

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, GenericRecord> offerFulfillmentKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, GenericRecord> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, GenericRecord> perksEnrollmentEventListenerContainerFactory(
          final EnrollmentEventFilterStrategy enrollmentEventFilterStrategy) {
    ConcurrentKafkaListenerContainerFactory<String, GenericRecord> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setRecordFilterStrategy(enrollmentEventFilterStrategy);

    return factory;
  }

  @Bean
  public EnrollmentEventFilterStrategy enrollmentEventFilterStrategy() {
    return new EnrollmentEventFilterStrategy();
  }

  private ConsumerFactory<String, GenericRecord> consumerFactory() {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, GenericRecord.class);
    return new DefaultKafkaConsumerFactory<>(props);
  }

}
