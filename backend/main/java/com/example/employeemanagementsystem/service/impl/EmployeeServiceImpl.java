package com.example.employeemanagementsystem.service.impl;

import com.example.employeemanagementsystem.dto.BankAccountDTO;
import com.example.employeemanagementsystem.dto.EmployeeDTO;
import com.example.employeemanagementsystem.entity.BankAccount;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.enums.Grade;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.exception.ValidationException;
import com.example.employeemanagementsystem.mapper.EntityMapper;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EntityMapper entityMapper;

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        if (employeeRepository.existsByEmployeeId(employeeDTO.getEmployeeId())) {
            throw new ValidationException("Employee ID already exists: " + employeeDTO.getEmployeeId());
        }

        if (!canAddEmployeeToGrade(employeeDTO.getGrade())) {
            throw new ValidationException("Maximum employees reached for grade: " + employeeDTO.getGrade());
        }

        Employee employee = entityMapper.toEmployee(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        return entityMapper.toEmployeeDTO(savedEmployee);
    }

    @Override
    public EmployeeDTO updateEmployee(String employeeId, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        // Check grade change
        if (!employee.getGrade().equals(employeeDTO.getGrade())) {
            if (!canAddEmployeeToGrade(employeeDTO.getGrade())) {
                throw new ValidationException("Maximum employees reached for grade: " + employeeDTO.getGrade());
            }
        }

        employee.setName(employeeDTO.getName());
        employee.setGrade(employeeDTO.getGrade());
        employee.setAddress(employeeDTO.getAddress());
        employee.setMobileNumber(employeeDTO.getMobileNumber());

        if (employeeDTO.getBankAccount() != null) {
            BankAccount bankAccount = employee.getBankAccount();
            if (bankAccount == null) {
                bankAccount = new BankAccount();
                employee.setBankAccount(bankAccount);
            }
            entityMapper.updateBankAccount(bankAccount, employeeDTO.getBankAccount());
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return entityMapper.toEmployeeDTO(updatedEmployee);
    }

    @Override
    public EmployeeDTO getEmployee(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
        return entityMapper.toEmployeeDTO(employee);
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAllOrderByGrade().stream()
                .map(entityMapper::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEmployee(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        // Delete the employee (bank account will be cascade deleted)
        employeeRepository.delete(employee);
        employeeRepository.flush(); // Force immediate execution
    }

    @Override
    public List<EmployeeDTO> getEmployeesByGrade(Grade grade) {
        return employeeRepository.findByGrade(grade).stream()
                .map(entityMapper::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean canAddEmployeeToGrade(Grade grade) {
        Long currentCount = employeeRepository.countByGrade(grade);
        return currentCount < grade.getMaxEmployees();
    }

    @Override
    public Employee getEmployeeEntity(String employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
    }
}
