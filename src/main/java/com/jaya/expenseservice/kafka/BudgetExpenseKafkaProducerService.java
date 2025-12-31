package com.jaya.expenseservice.kafka;

import com.jaya.budgetservice.events.BudgetExpenseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BudgetExpenseKafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(BudgetExpenseKafkaProducerService.class);
    private static final String BUDGET_EXPENSE_TOPIC = "budget-expense-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BudgetExpenseKafkaProducerService(@Qualifier("expenseObjectKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBudgetExpenseEvent(BudgetExpenseEvent event) {


        try {
            kafkaTemplate.send(BUDGET_EXPENSE_TOPIC, event);
            logger.info("Budget expense event sent for expense ID: {} and user: {}",
                    event.getExpenseId(), event.getUserId());
        } catch (Exception e) {
            logger.error("Failed to send budget expense event for expense ID: {}",
                    event.getExpenseId(), e);
        }
    }
}