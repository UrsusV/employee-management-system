// EmployeeController.java
package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.EmployeeDTO;
import com.example.employeemanagementsystem.enums.Grade;
import com.example.employeemanagementsystem.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO created = employeeService.createEmployee(employeeDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable String employeeId,
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO updated = employeeService.updateEmployee(employeeId, employeeDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable String employeeId) {
        EmployeeDTO employee = employeeService.getEmployee(employeeId);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/grade/{grade}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByGrade(@PathVariable Grade grade) {
        List<EmployeeDTO> employees = employeeService.getEmployeesByGrade(grade);
        return ResponseEntity.ok(employees);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grade/{grade}/can-add")
    public ResponseEntity<Boolean> canAddEmployeeToGrade(@PathVariable Grade grade) {
        boolean canAdd = employeeService.canAddEmployeeToGrade(grade);
        return ResponseEntity.ok(canAdd);
    }
}
