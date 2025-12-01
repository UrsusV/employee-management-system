package com.example.employeemanagementsystem.service.impl;

import com.example.employeemanagementsystem.dto.SalaryCalculationDTO;
import com.example.employeemanagementsystem.entity.Company;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.enums.Grade;
import com.example.employeemanagementsystem.service.SalaryCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.example.employeemanagementsystem.constants.AppConstants.*;

@Service
@RequiredArgsConstructor
public class SalaryCalculationServiceImpl implements SalaryCalculationService {

    @Override
    public BigDecimal calculateBasicSalary(Grade grade, BigDecimal baseSalary) {
        int gradeLevel = grade.getLevel();
        int incrementCount = 6 - gradeLevel; // Grade 6 = 0 increments, Grade 1 = 5 increments

        BigDecimal calculatedSalary = baseSalary;
        for (int i = 0; i < incrementCount; i++) {
            calculatedSalary = calculatedSalary.add(SALARY_INCREMENT);
        }

        return calculatedSalary;
    }

    @Override
    public SalaryCalculationDTO calculateSalary(Employee employee, BigDecimal baseSalary) {
        BigDecimal basicSalary = calculateBasicSalary(employee.getGrade(), baseSalary);
        BigDecimal houseRent = basicSalary.multiply(HOUSE_RENT_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal medicalAllowance = basicSalary.multiply(MEDICAL_ALLOWANCE_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalSalary = basicSalary.add(houseRent).add(medicalAllowance);

        SalaryCalculationDTO dto = new SalaryCalculationDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setEmployeeName(employee.getName());
        dto.setGrade(employee.getGrade().name());
        dto.setBasicSalary(basicSalary);
        dto.setHouseRent(houseRent);
        dto.setMedicalAllowance(medicalAllowance);
        dto.setTotalSalary(totalSalary);

        return dto;
    }

    @Override
    public List<SalaryCalculationDTO> calculateSalariesForEmployees(List<Employee> employees, Company company) {
        BigDecimal baseSalary = company.getBaseSalaryLowestGrade();
        List<SalaryCalculationDTO> salaryCalculations = new ArrayList<>();

        for (Employee employee : employees) {
            salaryCalculations.add(calculateSalary(employee, baseSalary));
        }

        return salaryCalculations;
    }
}

