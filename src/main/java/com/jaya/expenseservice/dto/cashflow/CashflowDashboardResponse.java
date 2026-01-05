package com.jaya.expenseservice.dto.cashflow;

import com.jaya.expenseservice.dto.ExpenseDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CashflowDashboardResponse {
    private List<ExpenseDTO> rawExpenses = new ArrayList<>();
    private List<CashflowChartBucketDTO> chartData = new ArrayList<>();
    private List<CashflowCardDTO> cardData = new ArrayList<>();
    private CashflowTotalsDTO totals = new CashflowTotalsDTO();
    private CashflowRangeContextDTO rangeContext;
    private String xKey;
}
