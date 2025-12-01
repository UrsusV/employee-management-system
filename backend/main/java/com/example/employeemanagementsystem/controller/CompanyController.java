package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.CompanyDTO;
import com.example.employeemanagementsystem.dto.FundAdditionDTO;
import com.example.employeemanagementsystem.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<CompanyDTO> getCompany() {
        CompanyDTO company = companyService.getCompany();
        return ResponseEntity.ok(company);
    }

    @PutMapping
    public ResponseEntity<CompanyDTO> updateCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        CompanyDTO updated = companyService.updateCompany(companyDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getCompanyBalance() {
        BigDecimal balance = companyService.getCompanyBalance();
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("balance", balance);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-funds")
    public ResponseEntity<Map<String, String>> addFunds(@Valid @RequestBody FundAdditionDTO fundAdditionDTO) {
        companyService.addFunds(fundAdditionDTO);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Funds added successfully");
        response.put("amount", fundAdditionDTO.getAmount().toString());
        return ResponseEntity.ok(response);
    }
}
