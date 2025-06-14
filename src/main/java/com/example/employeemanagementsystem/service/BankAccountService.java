package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.BankAccountDTO;
import java.math.BigDecimal;

public interface BankAccountService {
    BankAccountDTO createBankAccount(BankAccountDTO bankAccountDTO);
    BankAccountDTO updateBankAccount(Long id, BankAccountDTO bankAccountDTO);
    BankAccountDTO getBankAccount(Long id);
    void updateBalance(Long accountId, BigDecimal newBalance);
    boolean transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount);
}