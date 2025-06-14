package com.example.employeemanagementsystem.repository;

import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.enums.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findByGrade(Grade grade);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.grade = ?1")
    Long countByGrade(Grade grade);

    boolean existsByEmployeeId(String employeeId);

    @Query("SELECT e FROM Employee e ORDER BY e.grade ASC")
    List<Employee> findAllOrderByGrade();
}
