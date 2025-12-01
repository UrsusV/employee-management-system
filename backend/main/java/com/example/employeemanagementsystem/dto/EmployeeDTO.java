// EmployeeDTO.java
package com.example.employeemanagementsystem.dto;

import com.example.employeemanagementsystem.enums.Grade;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    @Pattern(regexp = "^[0-9]{4}$", message = "Employee ID must be exactly 4 digits")
    private String employeeId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Grade is required")
    private Grade grade;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must be between 10-15 digits")
    private String mobileNumber;

    private BankAccountDTO bankAccount;
}