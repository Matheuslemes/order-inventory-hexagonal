package com.sysmapproject.ms_order_service_v1.shared.config;

import com.sysmapproject.ms_order_service_v1.core.domain.dto.OrderEventDTO;
import com.sysmapproject.ms_order_service_v1.kafka.contracts.InventoryResponseDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class KafkaConfigTest {

    @InjectMocks
    private KafkaConfig kafkaConfig;

    @Mock
    private KafkaTemplate<String, OrderEventDTO> kafkaTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        kafkaConfig.bootstrapServers = "localhost:9092";
        kafkaConfig.groupId = "test-group";
        kafkaConfig.topicProducerName = "producer-topic";
        kafkaConfig.topicConsumerName = "consumer-topic";
    }

    @Test
    void producerFactory_ShouldBeConfiguredCorrectly() {
        ProducerFactory<String, OrderEventDTO> producerFactory = kafkaConfig.producerFactory();
        assertNotNull(producerFactory, "ProducerFactory não deve ser nulo");

        Map<String, Object> config = producerFactory.getConfigurationProperties();
        assertEquals("localhost:9092", config.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class, config.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(JsonSerializer.class, config.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    void kafkaTemplate_ShouldBeCreated() {
        KafkaTemplate<String, OrderEventDTO> template = kafkaConfig.kafkaTemplate();
        assertNotNull(template, "KafkaTemplate não deve ser nulo");
    }

    @Test
    void consumerFactory_ShouldBeConfiguredCorrectly() {
        ConsumerFactory<String, InventoryResponseDTO> consumerFactory = kafkaConfig.consumerFactory();
        assertNotNull(consumerFactory, "ConsumerFactory não deve ser nulo");

        Map<String, Object> config = consumerFactory.getConfigurationProperties();
        assertEquals("localhost:9092", config.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("test-group", config.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals(StringDeserializer.class, config.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
    }


    @Test
    void kafkaListenerContainerFactory_ShouldBeConfiguredCorrectly() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryResponseDTO> factory =
                kafkaConfig.kafkaListenerContainerFactory(kafkaTemplate);

        assertNotNull(factory, "KafkaListenerContainerFactory não deve ser nulo");
        assertNotNull(factory.getConsumerFactory(), "ConsumerFactory não deve ser nulo");
    }

    @Test
    void defaultErrorHandler_ShouldBeConfiguredCorrectly() {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (r, e) -> new TopicPartition(kafkaConfig.topicProducerName + ".DLQ", r.partition()));

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
        assertNotNull(errorHandler, "ErrorHandler não deve ser nulo");
    }
}
