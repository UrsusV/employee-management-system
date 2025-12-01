package com.example.employeemanagementsystem.service.impl;

import com.example.employeemanagementsystem.dto.BankAccountDTO;
import com.example.employeemanagementsystem.entity.BankAccount;
import com.example.employeemanagementsystem.exception.InsufficientFundsException;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.EntityMapper;
import com.example.employeemanagementsystem.repository.BankAccountRepository;
import com.example.employeemanagementsystem.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final EntityMapper entityMapper;

    @Override
    public BankAccountDTO createBankAccount(BankAccountDTO bankAccountDTO) {
        BankAccount bankAccount = entityMapper.toBankAccount(bankAccountDTO);
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        return entityMapper.toBankAccountDTO(savedAccount);
    }

    @Override
    public BankAccountDTO updateBankAccount(Long id, BankAccountDTO bankAccountDTO) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found: " + id));

        entityMapper.updateBankAccount(bankAccount, bankAccountDTO);
        BankAccount updatedAccount = bankAccountRepository.save(bankAccount);
        return entityMapper.toBankAccountDTO(updatedAccount);
    }

    @Override
    public BankAccountDTO getBankAccount(Long id) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found: " + id));
        return entityMapper.toBankAccountDTO(bankAccount);
    }

    @Override
    public void updateBalance(Long accountId, BigDecimal newBalance) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found: " + accountId));
        bankAccount.setCurrentBalance(newBalance);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public boolean transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Retry logic for optimistic locking
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                BankAccount fromAccount = bankAccountRepository.findById(fromAccountId)
                        .orElseThrow(() -> new ResourceNotFoundException("Source bank account not found: " + fromAccountId));

                BankAccount toAccount = bankAccountRepository.findById(toAccountId)
                        .orElseThrow(() -> new ResourceNotFoundException("Destination bank account not found: " + toAccountId));

                if (fromAccount.getCurrentBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException("Insufficient funds in source account");
                }

                fromAccount.setCurrentBalance(fromAccount.getCurrentBalance().subtract(amount));
                toAccount.setCurrentBalance(toAccount.getCurrentBalance().add(amount));

                bankAccountRepository.save(fromAccount);
                bankAccountRepository.save(toAccount);

                return true;
            } catch (ObjectOptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new RuntimeException("Failed to transfer funds after " + maxRetries + " retries due to concurrent modification", e);
                }
                // Wait briefly before retry
                try {
                    Thread.sleep(50 * retryCount); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Transfer interrupted", ie);
                }
            }
        }
        
        return false;
    }
}