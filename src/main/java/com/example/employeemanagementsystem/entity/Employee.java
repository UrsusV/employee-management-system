package com.example.employeemanagementsystem.entity;

import com.example.employeemanagementsystem.enums.Grade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @Column(name = "employee_id", length = 4)
    private String employeeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_account_id", unique = true)
    private BankAccount bankAccount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}