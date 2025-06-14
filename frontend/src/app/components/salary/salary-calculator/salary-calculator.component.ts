import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SalaryService } from '../../../services/salary.service';
import { NotificationService } from '../../../services/notification.service';
import { SalaryCalculation } from '../../../models/salary.model';
import { CurrencyFormatPipe } from '../../../pipes/currency-format.pipe';
import { GradeDisplayPipe } from '../../../pipes/grade-display.pipe';

@Component({
  selector: 'app-salary-calculator',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyFormatPipe,
    GradeDisplayPipe
  ],
  templateUrl: './salary-calculator.component.html',
  styleUrls: ['./salary-calculator.component.css']
})
export class SalaryCalculatorComponent implements OnInit {
  salaryCalculations: SalaryCalculation[] = [];
  salaryStructure: any[] = [];
  loading = false;

  constructor(
    private salaryService: SalaryService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadSalaryStructure();
  }

  calculateAllSalaries(): void {
    this.loading = true;
    this.salaryService.calculateAllSalaries().subscribe({
      next: (calculations) => {
        this.salaryCalculations = calculations;
        this.loading = false;
        this.notificationService.success('Salaries calculated successfully');
      },
      error: (error) => {
        this.notificationService.error('Failed to calculate salaries');
        this.loading = false;
      }
    });
  }

  getTotalSalary(): number {
    return this.salaryCalculations.reduce((sum, calc) => sum + calc.totalSalary, 0);
  }

  private loadSalaryStructure(): void {
    const grades = ['GRADE_1', 'GRADE_2', 'GRADE_3', 'GRADE_4', 'GRADE_5', 'GRADE_6'];
    const baseSalaries = [55000, 50000, 45000, 40000, 35000, 30000]; // Assuming base salary is 30000

    this.salaryStructure = grades.map((grade, index) => {
      const basicSalary = baseSalaries[index];
      const houseRent = basicSalary * 0.20;
      const medicalAllowance = basicSalary * 0.15;
      return {
        grade,
        basicSalary,
        houseRent,
        medicalAllowance,
        total: basicSalary + houseRent + medicalAllowance
      };
    });
  }
}
