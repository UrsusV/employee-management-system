package com.example.employeemanagementsystem.service.impl;

import com.example.employeemanagementsystem.dto.SalaryCalculationDTO;
import com.example.employeemanagementsystem.dto.SalarySheetDTO;
import com.example.employeemanagementsystem.entity.Company;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.entity.SalaryTransaction;
import com.example.employeemanagementsystem.enums.Grade;
import com.example.employeemanagementsystem.enums.TransactionStatus;
import com.example.employeemanagementsystem.exception.InsufficientFundsException;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.repository.CompanyRepository;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.SalaryTransactionRepository;
import com.example.employeemanagementsystem.service.BankAccountService;
import com.example.employeemanagementsystem.service.CompanyService;
import com.example.employeemanagementsystem.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryServiceImpl implements SalaryService {
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final SalaryTransactionRepository salaryTransactionRepository;
    private final BankAccountService bankAccountService;
    private final CompanyService companyService;

    private static final BigDecimal HOUSE_RENT_PERCENTAGE = new BigDecimal("0.20");
    private static final BigDecimal MEDICAL_ALLOWANCE_PERCENTAGE = new BigDecimal("0.15");
    private static final BigDecimal SALARY_INCREMENT = new BigDecimal("5000");

    @Override
    public BigDecimal calculateBasicSalary(Grade grade) {
        Company company = companyRepository.findByCompanyCode("MAIN")
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        BigDecimal baseSalary = company.getBaseSalaryLowestGrade();

        // Calculate based on grade level (Grade 6 is lowest, Grade 1 is highest)
        int gradeLevel = grade.getLevel();
        int incrementCount = 6 - gradeLevel; // Grade 6 = 0 increments, Grade 1 = 5 increments

        for (int i = 0; i < incrementCount; i++) {
            baseSalary = baseSalary.add(SALARY_INCREMENT);
        }

        return baseSalary;
    }

    @Override
    public SalaryCalculationDTO calculateSalary(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        BigDecimal basicSalary = calculateBasicSalary(employee.getGrade());
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
    public List<SalaryCalculationDTO> calculateAllSalaries() {
        List<Employee> employees = employeeRepository.findAllOrderByGrade();
        List<SalaryCalculationDTO> salaryCalculations = new ArrayList<>();

        for (Employee employee : employees) {
            salaryCalculations.add(calculateSalary(employee.getEmployeeId()));
        }

        return salaryCalculations;
    }

    @Override
    public SalarySheetDTO processSalaryPayment() {
        Company company = companyRepository.findByCompanyCode("MAIN")
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        List<Employee> employees = employeeRepository.findAllOrderByGrade();
        List<SalaryCalculationDTO> salaryDetails = new ArrayList<>();
        BigDecimal totalSalaryAmount = BigDecimal.ZERO;
        BigDecimal companyBalanceBefore = company.getBankAccount().getCurrentBalance();

        LocalDateTime transactionDate = LocalDateTime.now();
        String batchReference = "SALARY-" + UUID.randomUUID().toString().substring(0, 8);

        for (Employee employee : employees) {
            SalaryCalculationDTO salaryCalc = calculateSalary(employee.getEmployeeId());
            salaryDetails.add(salaryCalc);

            // Check if company has sufficient funds
            if (companyService.hasInsufficientFunds(salaryCalc.getTotalSalary())) {
                throw new InsufficientFundsException("Insufficient funds in company account. Required: " +
                        salaryCalc.getTotalSalary() + ", Available: " + companyService.getCompanyBalance());
            }

            // Transfer funds
            boolean transferred = bankAccountService.transferFunds(
                    company.getBankAccount().getId(),
                    employee.getBankAccount().getId(),
                    salaryCalc.getTotalSalary()
            );

            // Create transaction record
            SalaryTransaction transaction = new SalaryTransaction();
            transaction.setEmployee(employee);
            transaction.setBasicSalary(salaryCalc.getBasicSalary());
            transaction.setHouseRent(salaryCalc.getHouseRent());
            transaction.setMedicalAllowance(salaryCalc.getMedicalAllowance());
            transaction.setTotalSalary(salaryCalc.getTotalSalary());
            transaction.setStatus(transferred ? TransactionStatus.COMPLETED : TransactionStatus.FAILED);
            transaction.setTransactionDate(transactionDate);
            transaction.setTransactionReference(batchReference);
            salaryTransactionRepository.save(transaction);

            if (transferred) {
                totalSalaryAmount = totalSalaryAmount.add(salaryCalc.getTotalSalary());
            }
        }

        BigDecimal companyBalanceAfter = companyService.getCompanyBalance();

        SalarySheetDTO salarySheet = new SalarySheetDTO();
        salarySheet.setSalaryDetails(salaryDetails);
        salarySheet.setTotalSalaryPaid(totalSalaryAmount);
        salarySheet.setCompanyBalanceBefore(companyBalanceBefore);
        salarySheet.setCompanyBalanceAfter(companyBalanceAfter);
        salarySheet.setProcessDate(transactionDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return salarySheet;
    }

    @Override
    public SalarySheetDTO processSalaryPaymentWithFundCheck() {
        try {
            return processSalaryPayment();
        } catch (InsufficientFundsException e) {
            throw e; // Controller will handle this and prompt for fund addition
        }
    }
}