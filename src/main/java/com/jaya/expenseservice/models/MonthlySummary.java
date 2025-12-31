package com.jaya.expenseservice.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class MonthlySummary {

    private BigDecimal totalAmount;
    private Map<String, BigDecimal> categoryBreakdown;  // Key: Category, Value: Total amount spent
    private BigDecimal balanceRemaining;
    private BigDecimal currentMonthCreditDue = BigDecimal.ZERO;
    private CashSummary cash;
    private BigDecimal creditPaid = BigDecimal.ZERO;
    private BigDecimal creditDue = BigDecimal.ZERO;
    private String creditDueMessage;
}
