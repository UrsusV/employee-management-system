package com.example.employeemanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalarySheetDTO {
    private List<SalaryCalculationDTO> salaryDetails;
    private BigDecimal totalSalaryPaid;
    private BigDecimal companyBalanceBefore;
    private BigDecimal companyBalanceAfter;
    private String processDate;
}