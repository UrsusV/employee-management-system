package com.example.employeemanagementsystem.service.impl;

import com.example.employeemanagementsystem.dto.CompanyDTO;
import com.example.employeemanagementsystem.dto.FundAdditionDTO;
import com.example.employeemanagementsystem.entity.Company;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.EntityMapper;
import com.example.employeemanagementsystem.repository.CompanyRepository;
import com.example.employeemanagementsystem.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

import static com.example.employeemanagementsystem.constants.AppConstants.DEFAULT_COMPANY_CODE;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final EntityMapper entityMapper;

    @Override
    public CompanyDTO getCompany() {
        Company company = companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return entityMapper.toCompanyDTO(company);
    }

    @Override
    public CompanyDTO updateCompany(CompanyDTO companyDTO) {
        Company company = companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        company.setBaseSalaryLowestGrade(companyDTO.getBaseSalaryLowestGrade());
        Company updatedCompany = companyRepository.save(company);
        return entityMapper.toCompanyDTO(updatedCompany);
    }

    @Override
    public BigDecimal getCompanyBalance() {
        Company company = companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return company.getBankAccount().getCurrentBalance();
    }

    @Override
    public void addFunds(FundAdditionDTO fundAdditionDTO) {
        Company company = companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        BigDecimal currentBalance = company.getBankAccount().getCurrentBalance();
        BigDecimal newBalance = currentBalance.add(fundAdditionDTO.getAmount());
        company.getBankAccount().setCurrentBalance(newBalance);

        companyRepository.save(company);
    }

    @Override
    public Company getCompanyEntity() {
        return companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
    }

    @Override
    public boolean hasInsufficientFunds(BigDecimal requiredAmount) {
        BigDecimal currentBalance = getCompanyBalance();
        return currentBalance.compareTo(requiredAmount) < 0;
    }
}
