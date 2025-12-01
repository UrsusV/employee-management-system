package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.SalaryCalculationDTO;
import com.example.employeemanagementsystem.entity.Company;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.enums.Grade;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service responsible for salary calculation logic.
 * Separated from payment processing for better modularity.
 */
public interface SalaryCalculationService {
    BigDecimal calculateBasicSalary(Grade grade, BigDecimal baseSalary);
    SalaryCalculationDTO calculateSalary(Employee employee, BigDecimal baseSalary);
    List<SalaryCalculationDTO> calculateSalariesForEmployees(List<Employee> employees, Company company);
}

