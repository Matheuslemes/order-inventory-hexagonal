package com.desafiosysmap.ms_inventory_service_v1.shared.config;

import com.desafiosysmap.ms_inventory_service_v1.kafka.contracts.OrderEventDTO;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConfigTest {

    private KafkaConfig kafkaConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaConfig = new KafkaConfig();

        kafkaConfig.bootstrapServers = "localhost:9092";
        kafkaConfig.groupId = "test-group";
        kafkaConfig.topicConsumerName = "test-consumer-topic";
        kafkaConfig.topicProducerName = "test-producer-topic";
    }

    @Test
    void shouldCreateOrderTopic() {
        NewTopic topic = kafkaConfig.orderTopic();

        assertNotNull(topic);
        assertEquals("test-consumer-topic", topic.name());
        assertEquals(1, topic.numPartitions());
        assertEquals(1, topic.replicationFactor());
    }

    @Test
    void shouldCreateInventoryValidatedTopic() {
        NewTopic topic = kafkaConfig.inventoryValidatedTopic();

        assertNotNull(topic);
        assertEquals("test-producer-topic", topic.name());
        assertEquals(1, topic.numPartitions());
        assertEquals(1, topic.replicationFactor());
    }

    @Test
    void shouldCreateKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderEventDTO> factory = kafkaConfig.orderKafkaListenerContainerFactory();

        assertNotNull(factory);
        assertNotNull(factory.getConsumerFactory());

        assertTrue(factory.getConsumerFactory().getValueDeserializer() instanceof JsonDeserializer);
    }

    @Test
    void shouldUseCorrectTrustedPackages() {
        ConsumerFactory<String, OrderEventDTO> factory = kafkaConfig.orderConsumerFactory();
        Map<String, Object> configProps = factory.getConfigurationProperties();

        String trustedPackages = (String) configProps.get(JsonDeserializer.TRUSTED_PACKAGES);
        assertNotNull(trustedPackages);
        assertTrue(trustedPackages.contains("com.sysmapproject.ms_order_service_v1.core.domain.entity"));
        assertTrue(trustedPackages.contains("com.sysmapproject.ms_order_service_v1.kafka.contracts"));
        assertTrue(trustedPackages.contains("com.desafiosysmap.ms_inventory_service_v1.kafka.contracts"));
    }

    @Test
    void shouldFailForNullGroupId() {
        kafkaConfig.groupId = null;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kafkaConfig.orderConsumerFactory();
        });

        String message = exception.getMessage() != null ? exception.getMessage() : "No message";
        assertTrue(message.contains("group.id") || message.contains("No message"));
    }
}
