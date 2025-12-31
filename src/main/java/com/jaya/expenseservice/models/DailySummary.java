	package com.jaya.expenseservice.models;

    import lombok.Data;

    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.util.Map;
	
	@Data
	public class DailySummary {
	    private LocalDate date;
	    private BigDecimal totalAmount;
	    private Map<String, BigDecimal> categoryBreakdown;
	    private BigDecimal balanceRemaining;
	    private BigDecimal currentMonthCreditDue;
	    private CashSummary cash;
	    private CreditDueSummary creditDueSummary;
	    private CreditPaidSummary creditPaidSummary;
	    private BigDecimal creditPaid;
	    private BigDecimal creditDue;
	    private String creditDueMessage;
	
	    // Getters and setters
	}
	
