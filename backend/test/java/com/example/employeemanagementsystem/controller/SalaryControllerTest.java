package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.FundAdditionDTO;
import com.example.employeemanagementsystem.dto.SalaryCalculationDTO;
import com.example.employeemanagementsystem.dto.SalarySheetDTO;
import com.example.employeemanagementsystem.enums.Grade;
import com.example.employeemanagementsystem.exception.InsufficientFundsException;
import com.example.employeemanagementsystem.service.CompanyService;
import com.example.employeemanagementsystem.service.SalaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SalaryController.class)
class SalaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalaryService salaryService;

    @MockBean
    private CompanyService companyService;

    @Autowired
    private ObjectMapper objectMapper;

    private SalaryCalculationDTO salaryCalculationDTO;
    private SalarySheetDTO salarySheetDTO;

    @BeforeEach
    void setUp() {
        salaryCalculationDTO = new SalaryCalculationDTO();
        salaryCalculationDTO.setEmployeeId("0001");
        salaryCalculationDTO.setEmployeeName("John Doe");
        salaryCalculationDTO.setGrade("GRADE_6");
        salaryCalculationDTO.setBasicSalary(BigDecimal.valueOf(20000));
        salaryCalculationDTO.setHouseRent(BigDecimal.valueOf(4000));
        salaryCalculationDTO.setMedicalAllowance(BigDecimal.valueOf(3000));
        salaryCalculationDTO.setTotalSalary(BigDecimal.valueOf(27000));

        salarySheetDTO = new SalarySheetDTO();
        salarySheetDTO.setSalaryDetails(Arrays.asList(salaryCalculationDTO));
        salarySheetDTO.setTotalSalaryPaid(BigDecimal.valueOf(27000));
        salarySheetDTO.setCompanyBalanceBefore(BigDecimal.valueOf(100000));
        salarySheetDTO.setCompanyBalanceAfter(BigDecimal.valueOf(73000));
        salarySheetDTO.setProcessDate("2024-01-01T00:00:00");
    }

    @Test
    void testCalculateEmployeeSalary_Success() throws Exception {
        when(salaryService.calculateSalary("0001")).thenReturn(salaryCalculationDTO);

        mockMvc.perform(get("/salary/calculate/0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("0001"))
                .andExpect(jsonPath("$.totalSalary").value(27000));

        verify(salaryService, times(1)).calculateSalary("0001");
    }

    @Test
    void testCalculateAllSalaries_Success() throws Exception {
        List<SalaryCalculationDTO> salaries = Arrays.asList(salaryCalculationDTO);
        when(salaryService.calculateAllSalaries()).thenReturn(salaries);

        mockMvc.perform(get("/salary/calculate-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].employeeId").value("0001"));

        verify(salaryService, times(1)).calculateAllSalaries();
    }

    @Test
    void testGetBasicSalary_Success() throws Exception {
        when(salaryService.calculateBasicSalary(Grade.GRADE_6)).thenReturn(BigDecimal.valueOf(20000));

        mockMvc.perform(get("/salary/basic-salary/GRADE_6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value("GRADE_6"))
                .andExpect(jsonPath("$.basicSalary").value(20000));

        verify(salaryService, times(1)).calculateBasicSalary(Grade.GRADE_6);
    }

    @Test
    void testProcessSalaryPayment_Success() throws Exception {
        when(salaryService.processSalaryPayment()).thenReturn(salarySheetDTO);

        mockMvc.perform(post("/salary/process-payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSalaryPaid").value(27000));

        verify(salaryService, times(1)).processSalaryPayment();
    }

    @Test
    void testProcessSalaryPayment_InsufficientFunds() throws Exception {
        when(salaryService.processSalaryPayment())
                .thenThrow(new InsufficientFundsException("Insufficient funds"));
        when(companyService.getCompanyBalance()).thenReturn(BigDecimal.valueOf(10000));

        mockMvc.perform(post("/salary/process-payment"))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.error").value("INSUFFICIENT_FUNDS"));

        verify(salaryService, times(1)).processSalaryPayment();
    }

    @Test
    void testProcessSalaryPaymentWithFunds_Success() throws Exception {
        FundAdditionDTO fundAdditionDTO = new FundAdditionDTO();
        fundAdditionDTO.setAmount(BigDecimal.valueOf(50000));

        doNothing().when(companyService).addFunds(any(FundAdditionDTO.class));
        when(salaryService.processSalaryPayment()).thenReturn(salarySheetDTO);

        mockMvc.perform(post("/salary/process-payment-with-funds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundAdditionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSalaryPaid").value(27000));

        verify(companyService, times(1)).addFunds(any(FundAdditionDTO.class));
        verify(salaryService, times(1)).processSalaryPayment();
    }

    @Test
    void testGenerateSalarySheet_Success() throws Exception {
        List<SalaryCalculationDTO> salaries = Arrays.asList(salaryCalculationDTO);
        when(salaryService.calculateAllSalaries()).thenReturn(salaries);
        when(companyService.getCompanyBalance()).thenReturn(BigDecimal.valueOf(100000));

        mockMvc.perform(get("/salary/sheet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salaryDetails").isArray());

        verify(salaryService, times(1)).calculateAllSalaries();
    }
}

