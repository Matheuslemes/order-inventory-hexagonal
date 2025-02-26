package com.desafiosysmap.ms_inventory_service_v1.shared.config;

import com.desafiosysmap.ms_inventory_service_v1.kafka.contracts.OrderEventDTO;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
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
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    public String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    public String groupId;

    @Value("${spring.kafka.topics.consumer-topic}")
    public String topicConsumerName;

    @Value("${spring.kafka.topics.producer-topic}")
    public String topicProducerName;

    @Bean
    public NewTopic orderTopic() {
        return new NewTopic(topicConsumerName, 1, (short) 1);
    }

    @Bean
    public NewTopic inventoryValidatedTopic() {
        return new NewTopic(topicProducerName, 1, (short) 1);
    }

    @Bean
    public ConsumerFactory<String, OrderEventDTO> orderConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES,
                        "com.sysmapproject.ms_order_service_v1.core.domain.entity, " +
                        "com.sysmapproject.ms_order_service_v1.kafka.contracts, " +
                        "com.desafiosysmap.ms_inventory_service_v1.kafka.contracts");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(OrderEventDTO.class, false));
    }

    @Bean(name = "orderEventKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OrderEventDTO> orderKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderEventDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderConsumerFactory());
        return factory;    }

}
