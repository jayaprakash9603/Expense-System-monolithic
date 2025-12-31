package com.jaya.expenseservice.models;

import com.jaya.userservice.modal.EmailLog;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "common_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @OneToOne
//    @JoinColumn(name = "audit_expense_id")
//    private AuditExpense auditExpense;

    @OneToOne
    @JoinColumn(name = "email_log_id")
    private EmailLog emailLog;

    private LocalDateTime timestamp;
}