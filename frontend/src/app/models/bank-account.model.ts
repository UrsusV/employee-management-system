import { AccountType } from './enums';

export interface BankAccount {
  id?: number;
  accountType: AccountType;
  accountName: string;
  accountNumber: string;
  currentBalance: number;
  bankName: string;
  branchName: string;
}
