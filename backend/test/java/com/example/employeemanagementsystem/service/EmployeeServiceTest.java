package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.BankAccountDTO;
import com.example.employeemanagementsystem.dto.EmployeeDTO;
import com.example.employeemanagementsystem.entity.BankAccount;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.enums.AccountType;
import com.example.employeemanagementsystem.enums.Grade;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.exception.ValidationException;
import com.example.employeemanagementsystem.mapper.EntityMapper;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeDTO employeeDTO;
    private Employee employee;
    private BankAccountDTO bankAccountDTO;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        bankAccountDTO = new BankAccountDTO();
        bankAccountDTO.setId(1L);
        bankAccountDTO.setAccountType(AccountType.SAVINGS);
        bankAccountDTO.setAccountNumber("1234567890");
        bankAccountDTO.setAccountName("John Doe");
        bankAccountDTO.setCurrentBalance(java.math.BigDecimal.valueOf(10000));
        bankAccountDTO.setBankName("Test Bank");
        bankAccountDTO.setBranchName("Test Branch");

        bankAccount = new BankAccount();
        bankAccount.setId(1L);
        bankAccount.setAccountType(AccountType.SAVINGS);
        bankAccount.setAccountNumber("1234567890");
        bankAccount.setAccountName("John Doe");
        bankAccount.setCurrentBalance(java.math.BigDecimal.valueOf(10000));
        bankAccount.setBankName("Test Bank");
        bankAccount.setBranchName("Test Branch");

        employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId("0001");
        employeeDTO.setName("John Doe");
        employeeDTO.setGrade(Grade.GRADE_6);
        employeeDTO.setAddress("123 Test St");
        employeeDTO.setMobileNumber("1234567890");
        employeeDTO.setBankAccount(bankAccountDTO);

        employee = new Employee();
        employee.setEmployeeId("0001");
        employee.setName("John Doe");
        employee.setGrade(Grade.GRADE_6);
        employee.setAddress("123 Test St");
        employee.setMobileNumber("1234567890");
        employee.setBankAccount(bankAccount);
    }

    @Test
    void testCreateEmployee_Success() {
        when(employeeRepository.existsByEmployeeId("0001")).thenReturn(false);
        when(employeeRepository.countByGrade(Grade.GRADE_6)).thenReturn(0L);
        when(entityMapper.toEmployee(employeeDTO)).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(entityMapper.toEmployeeDTO(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.createEmployee(employeeDTO);

        assertNotNull(result);
        assertEquals("0001", result.getEmployeeId());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testCreateEmployee_DuplicateId() {
        when(employeeRepository.existsByEmployeeId("0001")).thenReturn(true);

        assertThrows(ValidationException.class, () -> employeeService.createEmployee(employeeDTO));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testCreateEmployee_MaxEmployeesReached() {
        when(employeeRepository.existsByEmployeeId("0001")).thenReturn(false);
        when(employeeRepository.countByGrade(Grade.GRADE_6)).thenReturn(2L); // Max is 2

        assertThrows(ValidationException.class, () -> employeeService.createEmployee(employeeDTO));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee_Success() {
        EmployeeDTO updatedDTO = new EmployeeDTO();
        updatedDTO.setEmployeeId("0001");
        updatedDTO.setName("Jane Doe");
        updatedDTO.setGrade(Grade.GRADE_6);
        updatedDTO.setAddress("456 New St");
        updatedDTO.setMobileNumber("9876543210");

        when(employeeRepository.findById("0001")).thenReturn(Optional.of(employee));
        when(employeeRepository.countByGrade(Grade.GRADE_6)).thenReturn(1L);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(entityMapper.toEmployeeDTO(employee)).thenReturn(updatedDTO);

        EmployeeDTO result = employeeService.updateEmployee("0001", updatedDTO);

        assertNotNull(result);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee_NotFound() {
        when(employeeRepository.findById("0001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
                employeeService.updateEmployee("0001", employeeDTO));
    }

    @Test
    void testGetEmployee_Success() {
        when(employeeRepository.findById("0001")).thenReturn(Optional.of(employee));
        when(entityMapper.toEmployeeDTO(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.getEmployee("0001");

        assertNotNull(result);
        assertEquals("0001", result.getEmployeeId());
    }

    @Test
    void testGetEmployee_NotFound() {
        when(employeeRepository.findById("0001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployee("0001"));
    }

    @Test
    void testGetAllEmployees_Success() {
        Employee employee2 = new Employee();
        employee2.setEmployeeId("0002");
        employee2.setName("Jane Doe");
        employee2.setGrade(Grade.GRADE_5);

        EmployeeDTO employeeDTO2 = new EmployeeDTO();
        employeeDTO2.setEmployeeId("0002");
        employeeDTO2.setName("Jane Doe");
        employeeDTO2.setGrade(Grade.GRADE_5);

        when(employeeRepository.findAllOrderByGrade()).thenReturn(Arrays.asList(employee, employee2));
        when(entityMapper.toEmployeeDTO(employee)).thenReturn(employeeDTO);
        when(entityMapper.toEmployeeDTO(employee2)).thenReturn(employeeDTO2);

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteEmployee_Success() {
        when(employeeRepository.findById("0001")).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        assertDoesNotThrow(() -> employeeService.deleteEmployee("0001"));
        verify(employeeRepository, times(1)).delete(employee);
        verify(employeeRepository, times(1)).flush();
    }

    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeRepository.findById("0001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee("0001"));
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    void testGetEmployeesByGrade_Success() {
        when(employeeRepository.findByGrade(Grade.GRADE_6)).thenReturn(Arrays.asList(employee));
        when(entityMapper.toEmployeeDTO(employee)).thenReturn(employeeDTO);

        List<EmployeeDTO> result = employeeService.getEmployeesByGrade(Grade.GRADE_6);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testCanAddEmployeeToGrade_True() {
        when(employeeRepository.countByGrade(Grade.GRADE_6)).thenReturn(1L);

        boolean result = employeeService.canAddEmployeeToGrade(Grade.GRADE_6);

        assertTrue(result);
    }

    @Test
    void testCanAddEmployeeToGrade_False() {
        when(employeeRepository.countByGrade(Grade.GRADE_6)).thenReturn(2L); // Max is 2

        boolean result = employeeService.canAddEmployeeToGrade(Grade.GRADE_6);

        assertFalse(result);
    }
}

