package com.jaya.expenseservice.dto.cashflow;

import com.jaya.expenseservice.dto.ExpenseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CashflowCardDTO extends ExpenseDTO {
    private String dayLabel;
    private String monthLabel;
    private String weekLabel;
    private String isoDate;
    private double amount;
    private String name;
    private String comments;
    private String bucketLabel;
}
