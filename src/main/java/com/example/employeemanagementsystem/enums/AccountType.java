package com.example.employeemanagementsystem.enums;

public enum AccountType {
    SAVINGS("Savings Account"),
    CURRENT("Current Account");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
