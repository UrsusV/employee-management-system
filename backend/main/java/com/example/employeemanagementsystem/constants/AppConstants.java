package com.example.employeemanagementsystem.constants;

import java.math.BigDecimal;

public final class AppConstants {
    
    private AppConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    // Company Constants
    public static final String DEFAULT_COMPANY_CODE = "MAIN";
    
    // Salary Calculation Constants
    public static final BigDecimal HOUSE_RENT_PERCENTAGE = new BigDecimal("0.20");
    public static final BigDecimal MEDICAL_ALLOWANCE_PERCENTAGE = new BigDecimal("0.15");
    public static final BigDecimal SALARY_INCREMENT = new BigDecimal("5000");
    
    // Transaction Constants
    public static final String SALARY_BATCH_PREFIX = "SALARY-";
    public static final int BATCH_REFERENCE_LENGTH = 8;
}

