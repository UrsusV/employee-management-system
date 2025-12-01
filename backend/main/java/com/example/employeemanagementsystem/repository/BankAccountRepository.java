package com.example.employeemanagementsystem.repository;

import com.example.employeemanagementsystem.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}