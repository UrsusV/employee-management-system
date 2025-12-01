package com.example.employeemanagementsystem.dto;

import com.example.employeemanagementsystem.enums.AccountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDTO {
    private Long id;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name cannot exceed 100 characters")
    private String accountName;

    @NotBlank(message = "Account number is required")
    @Size(max = 50, message = "Account number cannot exceed 50 characters")
    private String accountNumber;

    @NotNull(message = "Current balance is required")
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal currentBalance;

    @NotBlank(message = "Bank name is required")
    @Size(max = 100, message = "Bank name cannot exceed 100 characters")
    private String bankName;

    @NotBlank(message = "Branch name is required")
    @Size(max = 100, message = "Branch name cannot exceed 100 characters")
    private String branchName;
}