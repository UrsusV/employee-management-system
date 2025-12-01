package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.SalaryCalculationDTO;
import com.example.employeemanagementsystem.dto.SalarySheetDTO;
import com.example.employeemanagementsystem.enums.Grade;
import java.math.BigDecimal;
import java.util.List;

public interface SalaryService {
    BigDecimal calculateBasicSalary(Grade grade);
    SalaryCalculationDTO calculateSalary(String employeeId);
    List<SalaryCalculationDTO> calculateAllSalaries();
    SalarySheetDTO processSalaryPayment();
    SalarySheetDTO processSalaryPaymentWithFundCheck();
}