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
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.SalaryTransactionRepository;
import com.example.employeemanagementsystem.service.BankAccountService;
import com.example.employeemanagementsystem.service.CompanyService;
import com.example.employeemanagementsystem.service.SalaryCalculationService;
import com.example.employeemanagementsystem.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.employeemanagementsystem.constants.AppConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryServiceImpl implements SalaryService {
    private final EmployeeRepository employeeRepository;
    private final SalaryTransactionRepository salaryTransactionRepository;
    private final BankAccountService bankAccountService;
    private final CompanyService companyService;
    private final SalaryCalculationService salaryCalculationService;

    @Override
    public BigDecimal calculateBasicSalary(Grade grade) {
        Company company = companyService.getCompanyEntity();
        BigDecimal baseSalary = company.getBaseSalaryLowestGrade();
        return salaryCalculationService.calculateBasicSalary(grade, baseSalary);
    }

    @Override
    public SalaryCalculationDTO calculateSalary(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
        Company company = companyService.getCompanyEntity();
        return salaryCalculationService.calculateSalary(employee, company.getBaseSalaryLowestGrade());
    }

    @Override
    public List<SalaryCalculationDTO> calculateAllSalaries() {
        List<Employee> employees = employeeRepository.findAllOrderByGrade();
        Company company = companyService.getCompanyEntity();
        // Single query for all employees with bank accounts, single query for company
        return salaryCalculationService.calculateSalariesForEmployees(employees, company);
    }

    @Override
    public SalarySheetDTO processSalaryPayment() {
        // Fetch company and employees once
        Company company = companyService.getCompanyEntity();
        List<Employee> employees = employeeRepository.findAllOrderByGrade();
        
        // Calculate all salaries in batch (no N+1 queries)
        List<SalaryCalculationDTO> salaryDetails = salaryCalculationService
                .calculateSalariesForEmployees(employees, company);
        
        // Calculate total required amount BEFORE processing
        BigDecimal totalRequiredAmount = salaryDetails.stream()
                .map(SalaryCalculationDTO::getTotalSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Check if company has sufficient funds for ALL salaries at once (fixes race condition)
        BigDecimal companyBalanceBefore = company.getBankAccount().getCurrentBalance();
        if (companyBalanceBefore.compareTo(totalRequiredAmount) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds in company account. Required: " + totalRequiredAmount +
                    ", Available: " + companyBalanceBefore);
        }

        LocalDateTime transactionDate = LocalDateTime.now();
        String batchReference = SALARY_BATCH_PREFIX + UUID.randomUUID().toString().substring(0, BATCH_REFERENCE_LENGTH);
        
        BigDecimal totalSalaryAmount = BigDecimal.ZERO;
        List<SalaryTransaction> transactions = new ArrayList<>();

        // Process all payments
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            SalaryCalculationDTO salaryCalc = salaryDetails.get(i);

            try {
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
                transactions.add(transaction);

                if (transferred) {
                    totalSalaryAmount = totalSalaryAmount.add(salaryCalc.getTotalSalary());
                }
            } catch (Exception e) {
                // Create failed transaction record
                SalaryTransaction transaction = new SalaryTransaction();
                transaction.setEmployee(employee);
                transaction.setBasicSalary(salaryCalc.getBasicSalary());
                transaction.setHouseRent(salaryCalc.getHouseRent());
                transaction.setMedicalAllowance(salaryCalc.getMedicalAllowance());
                transaction.setTotalSalary(salaryCalc.getTotalSalary());
                transaction.setStatus(TransactionStatus.FAILED);
                transaction.setTransactionDate(transactionDate);
                transaction.setTransactionReference(batchReference);
                transactions.add(transaction);
                throw new RuntimeException("Failed to process salary payment for employee: " + 
                        employee.getEmployeeId(), e);
            }
        }

        // Batch save all transactions
        salaryTransactionRepository.saveAll(transactions);

        // Refresh company to get updated balance
        company = companyService.getCompanyEntity();
        BigDecimal companyBalanceAfter = company.getBankAccount().getCurrentBalance();

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