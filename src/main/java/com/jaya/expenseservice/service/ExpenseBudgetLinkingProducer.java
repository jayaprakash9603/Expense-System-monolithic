package com.jaya.expenseservice.service;

import com.jaya.budgetservice.dto.ExpenseBudgetLinkingEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExpenseBudgetLinkingProducer {

    private static final String TOPIC = "expense-budget-linking-events";

    private final KafkaTemplate<String, Object> expenseObjectKafkaTemplate;

    public ExpenseBudgetLinkingProducer(
            @Qualifier("expenseObjectKafkaTemplate")
            KafkaTemplate<String, Object> expenseObjectKafkaTemplate) {
        this.expenseObjectKafkaTemplate = expenseObjectKafkaTemplate;
    }

    public void sendLinkingEvent(ExpenseBudgetLinkingEvent event) {
        String key = event.getUserId() != null
                ? String.valueOf(event.getUserId())
                : "linking-event";

        expenseObjectKafkaTemplate.send(TOPIC, key, event);
    }
}
