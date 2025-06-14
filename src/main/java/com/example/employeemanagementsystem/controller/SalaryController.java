package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.FundAdditionDTO;
import com.example.employeemanagementsystem.dto.SalaryCalculationDTO;
import com.example.employeemanagementsystem.dto.SalarySheetDTO;
import com.example.employeemanagementsystem.enums.Grade;
import com.example.employeemanagementsystem.exception.InsufficientFundsException;
import com.example.employeemanagementsystem.service.CompanyService;
import com.example.employeemanagementsystem.service.SalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/salary")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SalaryController {
    private final SalaryService salaryService;
    private final CompanyService companyService;

    @GetMapping("/calculate/{employeeId}")
    public ResponseEntity<SalaryCalculationDTO> calculateEmployeeSalary(@PathVariable String employeeId) {
        SalaryCalculationDTO salary = salaryService.calculateSalary(employeeId);
        return ResponseEntity.ok(salary);
    }

    @GetMapping("/calculate-all")
    public ResponseEntity<List<SalaryCalculationDTO>> calculateAllSalaries() {
        List<SalaryCalculationDTO> salaries = salaryService.calculateAllSalaries();
        return ResponseEntity.ok(salaries);
    }

    @GetMapping("/basic-salary/{grade}")
    public ResponseEntity<Map<String, Object>> getBasicSalary(@PathVariable Grade grade) {
        BigDecimal basicSalary = salaryService.calculateBasicSalary(grade);
        Map<String, Object> response = new HashMap<>();
        response.put("grade", grade);
        response.put("basicSalary", basicSalary);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process-payment")
    public ResponseEntity<?> processSalaryPayment() {
        try {
            SalarySheetDTO salarySheet = salaryService.processSalaryPayment();
            return ResponseEntity.ok(salarySheet);
        } catch (InsufficientFundsException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "INSUFFICIENT_FUNDS");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("currentBalance", companyService.getCompanyBalance());
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(errorResponse);
        }
    }

    @PostMapping("/process-payment-with-funds")
    public ResponseEntity<SalarySheetDTO> processSalaryPaymentWithFunds(@Valid @RequestBody FundAdditionDTO fundAdditionDTO) {
        companyService.addFunds(fundAdditionDTO);
        SalarySheetDTO salarySheet = salaryService.processSalaryPayment();
        return ResponseEntity.ok(salarySheet);
    }

    @GetMapping("/sheet")
    public ResponseEntity<SalarySheetDTO> generateSalarySheet() {
        // This generates a preview without actually processing payments
        List<SalaryCalculationDTO> salaries = salaryService.calculateAllSalaries();
        BigDecimal totalSalary = salaries.stream()
                .map(SalaryCalculationDTO::getTotalSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SalarySheetDTO sheet = new SalarySheetDTO();
        sheet.setSalaryDetails(salaries);
        sheet.setTotalSalaryPaid(BigDecimal.ZERO); // Not paid yet
        sheet.setCompanyBalanceBefore(companyService.getCompanyBalance());
        sheet.setCompanyBalanceAfter(companyService.getCompanyBalance());
        sheet.setProcessDate("Preview - Not Processed");

        return ResponseEntity.ok(sheet);
    }
}