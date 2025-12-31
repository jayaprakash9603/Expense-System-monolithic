package com.jaya.expenseservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaya.categoryservice.events.CategoryExpenseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CategoryExpenseKafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryExpenseKafkaProducerService.class);
    private static final String TOPIC = "category-expense-events";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    @Qualifier("expenseObjectMapper")
    private ObjectMapper objectMapper;

    public void sendCategoryExpenseEvent(CategoryExpenseEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, eventJson);
            logger.info("Category expense event sent successfully: {}", event);
        } catch (Exception e) {
            logger.error("Failed to send category expense event: {}", event, e);
        }
    }
}