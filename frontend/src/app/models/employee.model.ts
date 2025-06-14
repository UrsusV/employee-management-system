import { Grade } from './enums';
import { BankAccount } from './bank-account.model';

export interface Employee {
  employeeId: string;
  name: string;
  grade: Grade;
  address: string;
  mobileNumber: string;
  bankAccount: BankAccount;
}
