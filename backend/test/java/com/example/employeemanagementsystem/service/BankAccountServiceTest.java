package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.BankAccountDTO;
import com.example.employeemanagementsystem.entity.BankAccount;
import com.example.employeemanagementsystem.enums.AccountType;
import com.example.employeemanagementsystem.exception.InsufficientFundsException;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.EntityMapper;
import com.example.employeemanagementsystem.repository.BankAccountRepository;
import com.example.employeemanagementsystem.service.impl.BankAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private BankAccount bankAccount;
    private BankAccountDTO bankAccountDTO;

    @BeforeEach
    void setUp() {
        bankAccount = new BankAccount();
        bankAccount.setId(1L);
        bankAccount.setAccountType(AccountType.SAVINGS);
        bankAccount.setAccountNumber("1234567890");
        bankAccount.setAccountName("John Doe");
        bankAccount.setCurrentBalance(BigDecimal.valueOf(10000));
        bankAccount.setBankName("Test Bank");
        bankAccount.setBranchName("Test Branch");

        bankAccountDTO = new BankAccountDTO();
        bankAccountDTO.setId(1L);
        bankAccountDTO.setAccountType(AccountType.SAVINGS);
        bankAccountDTO.setAccountNumber("1234567890");
        bankAccountDTO.setAccountName("John Doe");
        bankAccountDTO.setCurrentBalance(BigDecimal.valueOf(10000));
        bankAccountDTO.setBankName("Test Bank");
        bankAccountDTO.setBranchName("Test Branch");
    }

    @Test
    void testCreateBankAccount_Success() {
        when(entityMapper.toBankAccount(bankAccountDTO)).thenReturn(bankAccount);
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);
        when(entityMapper.toBankAccountDTO(bankAccount)).thenReturn(bankAccountDTO);

        BankAccountDTO result = bankAccountService.createBankAccount(bankAccountDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void testUpdateBankAccount_Success() {
        BankAccountDTO updatedDTO = new BankAccountDTO();
        updatedDTO.setAccountName("Jane Doe");

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);
        when(entityMapper.toBankAccountDTO(bankAccount)).thenReturn(updatedDTO);

        BankAccountDTO result = bankAccountService.updateBankAccount(1L, updatedDTO);

        assertNotNull(result);
        verify(entityMapper, times(1)).updateBankAccount(bankAccount, updatedDTO);
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void testUpdateBankAccount_NotFound() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
                bankAccountService.updateBankAccount(1L, bankAccountDTO));
    }

    @Test
    void testGetBankAccount_Success() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(entityMapper.toBankAccountDTO(bankAccount)).thenReturn(bankAccountDTO);

        BankAccountDTO result = bankAccountService.getBankAccount(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetBankAccount_NotFound() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bankAccountService.getBankAccount(1L));
    }

    @Test
    void testUpdateBalance_Success() {
        BigDecimal newBalance = BigDecimal.valueOf(15000);

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);

        assertDoesNotThrow(() -> bankAccountService.updateBalance(1L, newBalance));

        assertEquals(newBalance, bankAccount.getCurrentBalance());
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void testTransferFunds_Success() {
        BankAccount fromAccount = new BankAccount();
        fromAccount.setId(1L);
        fromAccount.setCurrentBalance(BigDecimal.valueOf(10000));

        BankAccount toAccount = new BankAccount();
        toAccount.setId(2L);
        toAccount.setCurrentBalance(BigDecimal.valueOf(5000));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = bankAccountService.transferFunds(1L, 2L, BigDecimal.valueOf(3000));

        assertTrue(result);
        assertEquals(BigDecimal.valueOf(7000), fromAccount.getCurrentBalance());
        assertEquals(BigDecimal.valueOf(8000), toAccount.getCurrentBalance());
        verify(bankAccountRepository, times(2)).save(any(BankAccount.class));
    }

    @Test
    void testTransferFunds_InsufficientFunds() {
        BankAccount fromAccount = new BankAccount();
        fromAccount.setId(1L);
        fromAccount.setCurrentBalance(BigDecimal.valueOf(1000));

        BankAccount toAccount = new BankAccount();
        toAccount.setId(2L);
        toAccount.setCurrentBalance(BigDecimal.valueOf(5000));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        assertThrows(InsufficientFundsException.class, () -> 
                bankAccountService.transferFunds(1L, 2L, BigDecimal.valueOf(3000)));
    }

    @Test
    void testTransferFunds_OptimisticLockingRetry() {
        BankAccount fromAccount = new BankAccount();
        fromAccount.setId(1L);
        fromAccount.setCurrentBalance(BigDecimal.valueOf(10000));

        BankAccount toAccount = new BankAccount();
        toAccount.setId(2L);
        toAccount.setCurrentBalance(BigDecimal.valueOf(5000));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(BankAccount.class, 1L))
                .thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = bankAccountService.transferFunds(1L, 2L, BigDecimal.valueOf(3000));

        assertTrue(result);
        verify(bankAccountRepository, atLeast(2)).save(any(BankAccount.class));
    }

    @Test
    void testTransferFunds_FromAccountNotFound() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
                bankAccountService.transferFunds(1L, 2L, BigDecimal.valueOf(1000)));
    }

    @Test
    void testTransferFunds_ToAccountNotFound() {
        BankAccount fromAccount = new BankAccount();
        fromAccount.setId(1L);
        fromAccount.setCurrentBalance(BigDecimal.valueOf(10000));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
                bankAccountService.transferFunds(1L, 2L, BigDecimal.valueOf(1000)));
    }
}

