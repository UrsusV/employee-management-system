package com.example.employeemanagementsystem.entity;

import com.example.employeemanagementsystem.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "basic_salary", nullable = false, precision = 19, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "house_rent", nullable = false, precision = 19, scale = 2)
    private BigDecimal houseRent;

    @Column(name = "medical_allowance", nullable = false, precision = 19, scale = 2)
    private BigDecimal medicalAllowance;

    @Column(name = "total_salary", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalSalary;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}