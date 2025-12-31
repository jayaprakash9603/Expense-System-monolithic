package com.jaya.expenseservice.models;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class CashSummary {
	private BigDecimal gain = BigDecimal.ZERO;
	private BigDecimal loss = BigDecimal.ZERO;
	private BigDecimal difference = BigDecimal.ZERO;

	// Method to calculate the difference based on gain and loss
	public void calculateDifference() {
		this.difference = gain.add(loss).setScale(2, RoundingMode.HALF_UP);
	}
}
