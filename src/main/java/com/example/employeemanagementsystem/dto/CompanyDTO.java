package com.example.employeemanagementsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private Long id;
    private String companyCode;
    private BankAccountDTO bankAccount;

    @NotNull(message = "Base salary for lowest grade is required")
    @DecimalMin(value = "0.00", message = "Base salary cannot be negative")
    private BigDecimal baseSalaryLowestGrade;
}