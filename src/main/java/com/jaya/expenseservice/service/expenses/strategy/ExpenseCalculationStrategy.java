package com.jaya.expenseservice.service.expenses.strategy;



import com.jaya.expenseservice.models.Expense;
import com.jaya.expenseservice.service.expenses.vo.ExpenseCalculationResult;

import java.util.List;

/**
 * Strategy interface for expense calculations
 * Follows Strategy Pattern - defines family of algorithms
 */
public interface ExpenseCalculationStrategy {

    /**
     * Calculate expense-related metrics based on the strategy
     * 
     * @param expenses List of expenses to calculate
     * @return Calculation result containing totals and breakdowns
     */
    ExpenseCalculationResult calculate(List<Expense> expenses);

    /**
     * Get the name/type of this calculation strategy
     */
    String getStrategyName();
}
