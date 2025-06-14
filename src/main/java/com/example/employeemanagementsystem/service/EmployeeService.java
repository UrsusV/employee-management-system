package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.EmployeeDTO;
import com.example.employeemanagementsystem.enums.Grade;
import java.util.List;

public interface EmployeeService {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO updateEmployee(String employeeId, EmployeeDTO employeeDTO);
    EmployeeDTO getEmployee(String employeeId);
    List<EmployeeDTO> getAllEmployees();
    void deleteEmployee(String employeeId);
    List<EmployeeDTO> getEmployeesByGrade(Grade grade);
    boolean canAddEmployeeToGrade(Grade grade);
}
