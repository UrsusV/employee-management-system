package com.example.employeemanagementsystem.enums;

public enum Grade {
    GRADE_1(1, 1),
    GRADE_2(2, 1),
    GRADE_3(3, 2),
    GRADE_4(4, 2),
    GRADE_5(5, 2),
    GRADE_6(6, 2);

    private final int level;
    private final int maxEmployees;

    Grade(int level, int maxEmployees) {
        this.level = level;
        this.maxEmployees = maxEmployees;
    }

    public int getLevel() { return level; }
    public int getMaxEmployees() { return maxEmployees; }
}