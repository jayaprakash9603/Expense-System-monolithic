package com.jaya.categoryservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration("categoryKafkaListenerConfig")
@EnableKafka
public class CategoryKafkaListenerConfig {

    @Value("${app.kafka.category.concurrency:4}")
    private int concurrency;

    @Value("${spring.kafka.consumer.max-poll-records:500}")
    private int maxPollRecords;

    // Batch-enabled listener container factory for category events
    @Bean(name = "categoryBatchFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> categoryBatchFactory(
            @Qualifier("categoryConsumerFactory") ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        // Let Boot apply max.poll.records from properties; ensure it's present
        factory.getContainerProperties().getKafkaConsumerProperties()
                .put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, String.valueOf(maxPollRecords));
        return factory;
    }
}
