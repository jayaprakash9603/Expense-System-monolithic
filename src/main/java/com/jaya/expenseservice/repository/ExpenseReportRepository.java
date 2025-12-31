package com.jaya.expenseservice.repository;

import com.jaya.expenseservice.models.ExpenseReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseReportRepository extends JpaRepository<ExpenseReport, Integer> {
}
