package com.example.employeemanagementsystem.repository;

import com.example.employeemanagementsystem.entity.SalaryTransaction;
import com.example.employeemanagementsystem.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalaryTransactionRepository extends JpaRepository<SalaryTransaction, Long> {
    List<SalaryTransaction> findByEmployeeEmployeeId(String employeeId);

    List<SalaryTransaction> findByStatus(TransactionStatus status);

    List<SalaryTransaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT st FROM SalaryTransaction st WHERE st.transactionDate >= ?1 ORDER BY st.transactionDate DESC")
    List<SalaryTransaction> findRecentTransactions(LocalDateTime date);
}