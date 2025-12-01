package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.CompanyDTO;
import com.example.employeemanagementsystem.dto.FundAdditionDTO;
import com.example.employeemanagementsystem.entity.BankAccount;
import com.example.employeemanagementsystem.entity.Company;
import com.example.employeemanagementsystem.enums.AccountType;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.EntityMapper;
import com.example.employeemanagementsystem.repository.CompanyRepository;
import com.example.employeemanagementsystem.service.impl.CompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.employeemanagementsystem.constants.AppConstants.DEFAULT_COMPANY_CODE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private Company company;
    private CompanyDTO companyDTO;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        bankAccount = new BankAccount();
        bankAccount.setId(1L);
        bankAccount.setAccountType(AccountType.CURRENT);
        bankAccount.setAccountNumber("COMP001");
        bankAccount.setAccountName("Company Account");
        bankAccount.setCurrentBalance(BigDecimal.valueOf(100000));
        bankAccount.setBankName("Test Bank");
        bankAccount.setBranchName("Main Branch");

        company = new Company();
        company.setId(1L);
        company.setCompanyCode(DEFAULT_COMPANY_CODE);
        company.setBaseSalaryLowestGrade(BigDecimal.valueOf(20000));
        company.setBankAccount(bankAccount);

        companyDTO = new CompanyDTO();
        companyDTO.setId(1L);
        companyDTO.setCompanyCode(DEFAULT_COMPANY_CODE);
        companyDTO.setBaseSalaryLowestGrade(BigDecimal.valueOf(20000));
    }

    @Test
    void testGetCompany_Success() {
        when(companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE))
                .thenReturn(Optional.of(company));
        when(entityMapper.toCompanyDTO(company)).thenReturn(companyDTO);

        CompanyDTO result = companyService.getCompany();

        assertNotNull(result);
        assertEquals(DEFAULT_COMPANY_CODE, result.getCompanyCode());
    }

    @Test
    void testGetCompany_NotFound() {
        when(companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> companyService.getCompany());
    }

    @Test
    void testUpdateCompany_Success() {
        CompanyDTO updatedDTO = new CompanyDTO();
        updatedDTO.setBaseSalaryLowestGrade(BigDecimal.valueOf(25000));

        when(companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE))
                .thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(entityMapper.toCompanyDTO(company)).thenReturn(updatedDTO);

        CompanyDTO result = companyService.updateCompany(updatedDTO);

        assertNotNull(result);
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void testGetCompanyBalance_Success() {
        when(companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE))
                .thenReturn(Optional.of(company));

        BigDecimal result = companyService.getCompanyBalance();

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100000), result);
    }

    @Test
    void testAddFunds_Success() {
        FundAdditionDTO fundAdditionDTO = new FundAdditionDTO();
        fundAdditionDTO.setAmount(BigDecimal.valueOf(50000));

        when(companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE))
                .thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        assertDoesNotThrow(() -> companyService.addFunds(fundAdditionDTO));

        assertEquals(BigDecimal.valueOf(150000), company.getBankAccount().getCurrentBalance());
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void testHasInsufficientFunds_True() {
        when(companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE))
                .thenReturn(Optional.of(company));

        boolean result = companyService.hasInsufficientFunds(BigDecimal.valueOf(150000));

        assertTrue(result);
    }

    @Test
    void testHasInsufficientFunds_False() {
        when(companyRepository.findByCompanyCodeWithBankAccount(DEFAULT_COMPANY_CODE))
                .thenReturn(Optional.of(company));

        boolean result = companyService.hasInsufficientFunds(BigDecimal.valueOf(50000));

        assertFalse(result);
    }
}

