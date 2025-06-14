export interface SalaryCalculation {
  employeeId: string;
  employeeName: string;
  grade: string;
  basicSalary: number;
  houseRent: number;
  medicalAllowance: number;
  totalSalary: number;
}

export interface SalarySheet {
  salaryDetails: SalaryCalculation[];
  totalSalaryPaid: number;
  companyBalanceBefore: number;
  companyBalanceAfter: number;
  processDate: string;
}

export interface FundAddition {
  amount: number;
  description?: string;
}
