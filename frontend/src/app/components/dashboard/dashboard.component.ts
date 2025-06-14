import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';
import { EmployeeService } from '../../services/employee.service';
import { CompanyService } from '../../services/company.service';
import { Employee } from '../../models/employee.model';
import { Company } from '../../models/company.model';
import { Grade } from '../../models/enums';
import { LoadingSpinnerComponent } from '../shared/loading-spinner/loading-spinner.component';
import { CurrencyFormatPipe } from '../../pipes/currency-format.pipe';
import { GradeDisplayPipe } from '../../pipes/grade-display.pipe';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    LoadingSpinnerComponent,
    CurrencyFormatPipe,
    GradeDisplayPipe
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  employees: Employee[] = [];
  company: Company | null = null;
  companyBalance = 0;
  loading = true;
  gradeStats: any[] = [];

  constructor(
    private employeeService: EmployeeService,
    private companyService: CompanyService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private loadDashboardData(): void {
    forkJoin({
      employees: this.employeeService.getAllEmployees(),
      company: this.companyService.getCompany(),
      balance: this.companyService.getCompanyBalance()
    }).subscribe({
      next: (data) => {
        this.employees = data.employees;
        this.company = data.company;
        this.companyBalance = data.balance;
        this.calculateGradeStats();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading dashboard data:', error);
        this.loading = false;
      }
    });
  }

  private calculateGradeStats(): void {
    const grades = Object.values(Grade);
    const gradeMaxMap: { [key: string]: number } = {
      [Grade.GRADE_1]: 1,
      [Grade.GRADE_2]: 1,
      [Grade.GRADE_3]: 2,
      [Grade.GRADE_4]: 2,
      [Grade.GRADE_5]: 2,
      [Grade.GRADE_6]: 2
    };

    this.gradeStats = grades.map(grade => {
      const employeesInGrade = this.employees.filter(emp => emp.grade === grade);
      return {
        grade: grade,
        current: employeesInGrade.length,
        max: gradeMaxMap[grade]
      };
    });
  }
}
