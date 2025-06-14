package com.example.employeemanagementsystem.util;

import com.example.employeemanagementsystem.dto.BankAccountDTO;
import com.example.employeemanagementsystem.dto.CompanyDTO;
import com.example.employeemanagementsystem.dto.EmployeeDTO;
import com.example.employeemanagementsystem.entity.BankAccount;
import com.example.employeemanagementsystem.entity.Company;
import com.example.employeemanagementsystem.entity.Employee;

public class MapperUtil {

    // Employee mappings
    public static EmployeeDTO toEmployeeDTO(Employee employee) {
        if (employee == null) return null;

        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setName(employee.getName());
        dto.setGrade(employee.getGrade());
        dto.setAddress(employee.getAddress());
        dto.setMobileNumber(employee.getMobileNumber());
        dto.setBankAccount(toBankAccountDTO(employee.getBankAccount()));
        return dto;
    }

    public static Employee toEmployee(EmployeeDTO dto) {
        if (dto == null) return null;

        Employee employee = new Employee();
        employee.setEmployeeId(dto.getEmployeeId());
        employee.setName(dto.getName());
        employee.setGrade(dto.getGrade());
        employee.setAddress(dto.getAddress());
        employee.setMobileNumber(dto.getMobileNumber());

        if (dto.getBankAccount() != null) {
            employee.setBankAccount(toBankAccount(dto.getBankAccount()));
        }

        return employee;
    }

    // BankAccount mappings
    public static BankAccountDTO toBankAccountDTO(BankAccount bankAccount) {
        if (bankAccount == null) return null;

        BankAccountDTO dto = new BankAccountDTO();
        dto.setId(bankAccount.getId());
        dto.setAccountType(bankAccount.getAccountType());
        dto.setAccountName(bankAccount.getAccountName());
        dto.setAccountNumber(bankAccount.getAccountNumber());
        dto.setCurrentBalance(bankAccount.getCurrentBalance());
        dto.setBankName(bankAccount.getBankName());
        dto.setBranchName(bankAccount.getBranchName());
        return dto;
    }

    public static BankAccount toBankAccount(BankAccountDTO dto) {
        if (dto == null) return null;

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(dto.getId());
        bankAccount.setAccountType(dto.getAccountType());
        bankAccount.setAccountName(dto.getAccountName());
        bankAccount.setAccountNumber(dto.getAccountNumber());
        bankAccount.setCurrentBalance(dto.getCurrentBalance());
        bankAccount.setBankName(dto.getBankName());
        bankAccount.setBranchName(dto.getBranchName());
        return bankAccount;
    }

    public static void updateBankAccount(BankAccount bankAccount, BankAccountDTO dto) {
        if (bankAccount == null || dto == null) return;

        bankAccount.setAccountType(dto.getAccountType());
        bankAccount.setAccountName(dto.getAccountName());
        bankAccount.setAccountNumber(dto.getAccountNumber());
        bankAccount.setCurrentBalance(dto.getCurrentBalance());
        bankAccount.setBankName(dto.getBankName());
        bankAccount.setBranchName(dto.getBranchName());
    }

    // Company mappings
    public static CompanyDTO toCompanyDTO(Company company) {
        if (company == null) return null;

        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setCompanyCode(company.getCompanyCode());
        dto.setBankAccount(toBankAccountDTO(company.getBankAccount()));
        dto.setBaseSalaryLowestGrade(company.getBaseSalaryLowestGrade());
        return dto;
    }

    public static Company toCompany(CompanyDTO dto) {
        if (dto == null) return null;

        Company company = new Company();
        company.setId(dto.getId());
        company.setCompanyCode(dto.getCompanyCode());
        company.setBankAccount(toBankAccount(dto.getBankAccount()));
        company.setBaseSalaryLowestGrade(dto.getBaseSalaryLowestGrade());
        return company;
    }
}