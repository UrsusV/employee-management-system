import { BankAccount } from './bank-account.model';

export interface Company {
  id?: number;
  companyCode: string;
  bankAccount: BankAccount;
  baseSalaryLowestGrade: number;
}
