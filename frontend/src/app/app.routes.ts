import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { EmployeeListComponent } from './components/employee/employee-list/employee-list.component';
import { EmployeeFormComponent } from './components/employee/employee-form/employee-form.component';
import { CompanyManagementComponent } from './components/company/company-management/company-management.component';
import { SalaryCalculatorComponent } from './components/salary/salary-calculator/salary-calculator.component';
import { SalaryPaymentComponent } from './components/salary/salary-payment/salary-payment.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'employees', component: EmployeeListComponent },
  { path: 'employees/new', component: EmployeeFormComponent },
  { path: 'employees/edit/:id', component: EmployeeFormComponent },
  { path: 'company', component: CompanyManagementComponent },
  { path: 'salary/calculator', component: SalaryCalculatorComponent },
  { path: 'salary/payment', component: SalaryPaymentComponent },
  { path: '**', redirectTo: '/dashboard' }
];
