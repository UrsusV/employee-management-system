package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.SalaryCalculationDTO;
import com.example.employeemanagementsystem.entity.Company;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.enums.Grade;
import com.example.employeemanagementsystem.service.impl.SalaryCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.example.employeemanagementsystem.constants.AppConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SalaryCalculationServiceTest {

    @InjectMocks
    private SalaryCalculationServiceImpl salaryCalculationService;

    private Company company;
    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setBaseSalaryLowestGrade(BigDecimal.valueOf(20000));

        employee1 = new Employee();
        employee1.setEmployeeId("0001");
        employee1.setName("John Doe");
        employee1.setGrade(Grade.GRADE_6);

        employee2 = new Employee();
        employee2.setEmployeeId("0002");
        employee2.setName("Jane Doe");
        employee2.setGrade(Grade.GRADE_1);
    }

    @Test
    void testCalculateBasicSalary_Grade6() {
        BigDecimal baseSalary = BigDecimal.valueOf(20000);
        BigDecimal result = salaryCalculationService.calculateBasicSalary(Grade.GRADE_6, baseSalary);

        assertEquals(baseSalary, result);
    }

    @Test
    void testCalculateBasicSalary_Grade1() {
        BigDecimal baseSalary = BigDecimal.valueOf(20000);
        BigDecimal result = salaryCalculationService.calculateBasicSalary(Grade.GRADE_1, baseSalary);

        // Grade 1 should have 5 increments (6 - 1 = 5)
        BigDecimal expected = baseSalary.add(SALARY_INCREMENT.multiply(BigDecimal.valueOf(5)));
        assertEquals(expected, result);
    }

    @Test
    void testCalculateBasicSalary_Grade3() {
        BigDecimal baseSalary = BigDecimal.valueOf(20000);
        BigDecimal result = salaryCalculationService.calculateBasicSalary(Grade.GRADE_3, baseSalary);

        // Grade 3 should have 3 increments (6 - 3 = 3)
        BigDecimal expected = baseSalary.add(SALARY_INCREMENT.multiply(BigDecimal.valueOf(3)));
        assertEquals(expected, result);
    }

    @Test
    void testCalculateSalary_Success() {
        BigDecimal baseSalary = BigDecimal.valueOf(20000);
        SalaryCalculationDTO result = salaryCalculationService.calculateSalary(employee1, baseSalary);

        assertNotNull(result);
        assertEquals("0001", result.getEmployeeId());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals("GRADE_6", result.getGrade());
        assertNotNull(result.getBasicSalary());
        assertNotNull(result.getHouseRent());
        assertNotNull(result.getMedicalAllowance());
        assertNotNull(result.getTotalSalary());

        // Verify calculations
        BigDecimal expectedHouseRent = result.getBasicSalary().multiply(HOUSE_RENT_PERCENTAGE);
        BigDecimal expectedMedicalAllowance = result.getBasicSalary().multiply(MEDICAL_ALLOWANCE_PERCENTAGE);
        BigDecimal expectedTotal = result.getBasicSalary()
                .add(result.getHouseRent())
                .add(result.getMedicalAllowance());

        assertEquals(expectedHouseRent.setScale(2, java.math.RoundingMode.HALF_UP), result.getHouseRent());
        assertEquals(expectedMedicalAllowance.setScale(2, java.math.RoundingMode.HALF_UP), result.getMedicalAllowance());
        assertEquals(expectedTotal.setScale(2, java.math.RoundingMode.HALF_UP), result.getTotalSalary());
    }

    @Test
    void testCalculateSalariesForEmployees_Success() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        List<SalaryCalculationDTO> results = salaryCalculationService.calculateSalariesForEmployees(employees, company);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("0001", results.get(0).getEmployeeId());
        assertEquals("0002", results.get(1).getEmployeeId());

        // Verify Grade 1 salary is higher than Grade 6
        assertTrue(results.get(1).getBasicSalary().compareTo(results.get(0).getBasicSalary()) > 0);
    }

    @Test
    void testCalculateSalariesForEmployees_EmptyList() {
        List<Employee> employees = Arrays.asList();
        List<SalaryCalculationDTO> results = salaryCalculationService.calculateSalariesForEmployees(employees, company);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}

