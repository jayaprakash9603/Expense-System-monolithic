package com.jaya.expenseservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.jaya.budgetservice.dto.ExpenseBudgetLinkingEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration("expenseKafkaConfig")
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // String Producer Factory (Primary)
    @Bean(name = "expenseProducerFactory")
    @Primary
    public ProducerFactory<String, String> expenseProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // Object Producer Factory
    @Bean(name = "expenseObjectProducerFactory")
    public ProducerFactory<String, Object> expenseObjectProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        // Configure ObjectMapper for proper date serialization
        // configProps.put(JsonSerializer.OBJECT_MAPPER, expenseObjectMapper());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // String KafkaTemplate (Primary)
    @Bean(name = "expenseKafkaTemplate")
    @Primary
    public KafkaTemplate<String, String> expenseKafkaTemplate() {
        return new KafkaTemplate<>(expenseProducerFactory());
    }

    // Object KafkaTemplate
    @Bean(name = "expenseObjectKafkaTemplate")
    public KafkaTemplate<String, Object> expenseObjectKafkaTemplate() {
        return new KafkaTemplate<>(expenseObjectProducerFactory());
    }

    // Consumer Factory for String messages
    @Bean(name = "expenseConsumerFactory")
    public ConsumerFactory<String, String> expenseConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "expense-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // Object Consumer Factory for PaymentMethodEvent
    @Bean
    public ConsumerFactory<String, Object> objectConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-method-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.jaya.dto.PaymentMethodEvent");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Configure ObjectMapper for proper date deserialization
        // props.put(JsonDeserializer.OBJECT_MAPPER, objectMapper());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // UPDATED: Budget Expense Consumer Factory
    @Bean
    public ConsumerFactory<String, Object> budgetExpenseConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "budget-expense-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.jaya.budgetservice.events.BudgetExpenseEvent");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Configure ObjectMapper for proper date deserialization
        // props.put(JsonDeserializer.OBJECT_MAPPER, objectMapper());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // UPDATED: Category Event Consumer Factory
    @Bean(name = "expenseCategoryEventConsumerFactory")
    public ConsumerFactory<String, Object> expenseCategoryEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "category-expense-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.jaya.events.CategoryExpenseEvent");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Configure ObjectMapper for proper date deserialization
        // props.put(JsonDeserializer.OBJECT_MAPPER, objectMapper());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "expenseObjectMapper")
    public ObjectMapper expenseObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        // Disable writing dates as timestamps (arrays)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    // Default Kafka Listener Container Factory
    @Bean(name = "expenseKafkaListenerContainerFactory")
    @Primary
    public ConcurrentKafkaListenerContainerFactory<String, String> expenseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(expenseConsumerFactory());
        return factory;
    }

    // Object Kafka Listener Container Factory
    @Bean(name = "expenseObjectKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> expenseObjectKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(objectConsumerFactory());
        return factory;
    }

    // ADD THIS: Budget Expense Kafka Listener Container Factory
    @Bean(name = "budgetExpenseKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> budgetExpenseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(budgetExpenseConsumerFactory());
        return factory;
    }

    // ADD THIS: Category Event Kafka Listener Container Factory
    @Bean(name = "expenseCategoryEventKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> expenseCategoryEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(expenseCategoryEventConsumerFactory());
        return factory;
    }

    // Expense Budget Linking Consumer Factory
    @Bean
    public ConsumerFactory<String, ExpenseBudgetLinkingEvent> expenseBudgetLinkingConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "expense-service-linking-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Use ErrorHandlingDeserializer to wrap JsonDeserializer
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

    // IMPORTANT: deserialize into THIS service's DTO type. There is a similarly named
    // class in budgetservice; using that package will cause MessageConversionException
    // when Spring tries to pass the payload to a listener expecting
    // com.jaya.expenseservice.dto.ExpenseBudgetLinkingEvent.
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.jaya.expenseservice.dto.ExpenseBudgetLinkingEvent");
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.jaya.dto,com.jaya.events,com.jaya.expenseservice.dto,com.jaya.budgetservice.dto");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
        new ErrorHandlingDeserializer<>(new JsonDeserializer<>(ExpenseBudgetLinkingEvent.class)));
    }

    // Expense Budget Linking Kafka Listener Container Factory
    @Bean("expenseBudgetLinkingKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ExpenseBudgetLinkingEvent> expenseBudgetLinkingKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ExpenseBudgetLinkingEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(expenseBudgetLinkingConsumerFactory());
        return factory;
    }
}