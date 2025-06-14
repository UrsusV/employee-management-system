package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.CompanyDTO;
import com.example.employeemanagementsystem.dto.FundAdditionDTO;
import java.math.BigDecimal;

public interface CompanyService {
    CompanyDTO getCompany();
    CompanyDTO updateCompany(CompanyDTO companyDTO);
    BigDecimal getCompanyBalance();
    void addFunds(FundAdditionDTO fundAdditionDTO);
    boolean hasInsufficientFunds(BigDecimal requiredAmount);
}