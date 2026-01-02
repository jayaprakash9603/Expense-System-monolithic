package com.jaya.expenseservice.config;

import com.jaya.expenseservice.dto.ExpenseBudgetLinkingEvent;
import org.apache.kafka.common.serialization.Deserializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseBudgetLinkingKafkaConfigTest {

    @Test
    void jsonDeserializer_shouldDeserializeIntoExpenseServiceDto() {
        // Arrange
        String json = "{" +
                "\"eventType\":\"EXPENSE_BUDGET_LINK_UPDATE\"," +
                "\"userId\":2," +
                "\"newExpenseId\":52467," +
                "\"newBudgetId\":2," +
                "\"timestamp\":\"2026-01-02T13:47:54.394619800\"" +
                "}";

        Deserializer<ExpenseBudgetLinkingEvent> deserializer = new JsonDeserializer<>(ExpenseBudgetLinkingEvent.class);

        // Act
        ExpenseBudgetLinkingEvent event = deserializer.deserialize("expense-budget-linking-events", json.getBytes());

        // Assert
        assertThat(event).isNotNull();
        assertThat(event.getEventType()).isEqualTo(ExpenseBudgetLinkingEvent.EventType.EXPENSE_BUDGET_LINK_UPDATE);
        assertThat(event.getUserId()).isEqualTo(2L);
        assertThat(event.getNewExpenseId()).isEqualTo(52467L);
        assertThat(event.getNewBudgetId()).isEqualTo(2L);
    }
}
