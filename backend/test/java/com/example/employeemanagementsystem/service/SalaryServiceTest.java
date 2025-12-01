package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.SalaryCalculationDTO;
import com.example.employeemanagementsystem.dto.SalarySheetDTO;
import com.example.employeemanagementsystem.entity.BankAccount;
import com.example.employeemanagementsystem.entity.Company;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.enums.AccountType;
import com.example.employeemanagementsystem.enums.Grade;
import com.example.employeemanagementsystem.enums.TransactionStatus;
import com.example.employeemanagementsystem.exception.InsufficientFundsException;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.SalaryTransactionRepository;
import com.example.employeemanagementsystem.service.impl.SalaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SalaryTransactionRepository salaryTransactionRepository;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private CompanyService companyService;

    @Mock
    private SalaryCalculationService salaryCalculationService;

    @InjectMocks
    private SalaryServiceImpl salaryService;

    private Company company;
    private Employee employee1;
    private Employee employee2;
    private BankAccount companyBankAccount;
    private BankAccount employeeBankAccount1;
    private BankAccount employeeBankAccount2;
    private SalaryCalculationDTO salaryCalc1;
    private SalaryCalculationDTO salaryCalc2;

    @BeforeEach
    void setUp() {
        companyBankAccount = new BankAccount();
        companyBankAccount.setId(1L);
        companyBankAccount.setCurrentBalance(BigDecimal.valueOf(100000));

        employeeBankAccount1 = new BankAccount();
        employeeBankAccount1.setId(2L);
        employeeBankAccount1.setCurrentBalance(BigDecimal.ZERO);

        employeeBankAccount2 = new BankAccount();
        employeeBankAccount2.setId(3L);
        employeeBankAccount2.setCurrentBalance(BigDecimal.ZERO);

        company = new Company();
        company.setId(1L);
        company.setBaseSalaryLowestGrade(BigDecimal.valueOf(20000));
        company.setBankAccount(companyBankAccount);

        employee1 = new Employee();
        employee1.setEmployeeId("0001");
        employee1.setName("John Doe");
        employee1.setGrade(Grade.GRADE_6);
        employee1.setBankAccount(employeeBankAccount1);

        employee2 = new Employee();
        employee2.setEmployeeId("0002");
        employee2.setName("Jane Doe");
        employee2.setGrade(Grade.GRADE_5);
        employee2.setBankAccount(employeeBankAccount2);

        salaryCalc1 = new SalaryCalculationDTO();
        salaryCalc1.setEmployeeId("0001");
        salaryCalc1.setEmployeeName("John Doe");
        salaryCalc1.setTotalSalary(BigDecimal.valueOf(27000));

        salaryCalc2 = new SalaryCalculationDTO();
        salaryCalc2.setEmployeeId("0002");
        salaryCalc2.setEmployeeName("Jane Doe");
        salaryCalc2.setTotalSalary(BigDecimal.valueOf(32000));
    }

    @Test
    void testCalculateBasicSalary_Success() {
        when(companyService.getCompanyEntity()).thenReturn(company);
        when(salaryCalculationService.calculateBasicSalary(Grade.GRADE_6, company.getBaseSalaryLowestGrade()))
                .thenReturn(BigDecimal.valueOf(20000));

        BigDecimal result = salaryService.calculateBasicSalary(Grade.GRADE_6);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(20000), result);
    }

    @Test
    void testCalculateSalary_Success() {
        when(employeeRepository.findById("0001")).thenReturn(Optional.of(employee1));
        when(companyService.getCompanyEntity()).thenReturn(company);
        when(salaryCalculationService.calculateSalary(employee1, company.getBaseSalaryLowestGrade()))
                .thenReturn(salaryCalc1);

        SalaryCalculationDTO result = salaryService.calculateSalary("0001");

        assertNotNull(result);
        assertEquals("0001", result.getEmployeeId());
    }

    @Test
    void testCalculateSalary_EmployeeNotFound() {
        when(employeeRepository.findById("0001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> salaryService.calculateSalary("0001"));
    }

    @Test
    void testCalculateAllSalaries_Success() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        List<SalaryCalculationDTO> salaryCalcs = Arrays.asList(salaryCalc1, salaryCalc2);

        when(employeeRepository.findAllOrderByGrade()).thenReturn(employees);
        when(companyService.getCompanyEntity()).thenReturn(company);
        when(salaryCalculationService.calculateSalariesForEmployees(employees, company))
                .thenReturn(salaryCalcs);

        List<SalaryCalculationDTO> result = salaryService.calculateAllSalaries();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testProcessSalaryPayment_Success() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        List<SalaryCalculationDTO> salaryCalcs = Arrays.asList(salaryCalc1, salaryCalc2);
        BigDecimal totalRequired = BigDecimal.valueOf(59000); // 27000 + 32000

        when(employeeRepository.findAllOrderByGrade()).thenReturn(employees);
        when(companyService.getCompanyEntity()).thenReturn(company).thenReturn(company);
        when(salaryCalculationService.calculateSalariesForEmployees(employees, company))
                .thenReturn(salaryCalcs);
        when(bankAccountService.transferFunds(anyLong(), anyLong(), any(BigDecimal.class)))
                .thenReturn(true);
        when(salaryTransactionRepository.saveAll(anyList())).thenReturn(Arrays.asList());

        SalarySheetDTO result = salaryService.processSalaryPayment();

        assertNotNull(result);
        assertEquals(2, result.getSalaryDetails().size());
        assertEquals(totalRequired, result.getTotalSalaryPaid());
        verify(bankAccountService, times(2)).transferFunds(anyLong(), anyLong(), any(BigDecimal.class));
    }

    @Test
    void testProcessSalaryPayment_InsufficientFunds() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        List<SalaryCalculationDTO> salaryCalcs = Arrays.asList(salaryCalc1, salaryCalc2);
        companyBankAccount.setCurrentBalance(BigDecimal.valueOf(10000)); // Less than required

        when(employeeRepository.findAllOrderByGrade()).thenReturn(employees);
        when(companyService.getCompanyEntity()).thenReturn(company);
        when(salaryCalculationService.calculateSalariesForEmployees(employees, company))
                .thenReturn(salaryCalcs);

        assertThrows(InsufficientFundsException.class, () -> salaryService.processSalaryPayment());
        verify(bankAccountService, never()).transferFunds(anyLong(), anyLong(), any(BigDecimal.class));
    }
}

