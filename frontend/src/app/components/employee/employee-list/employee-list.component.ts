import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { EmployeeService } from '../../../services/employee.service';
import { NotificationService } from '../../../services/notification.service';
import { Employee } from '../../../models/employee.model';
import { Grade } from '../../../models/enums';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';
import { LoadingSpinnerComponent } from '../../shared/loading-spinner/loading-spinner.component';
import { GradeDisplayPipe } from '../../../pipes/grade-display.pipe';
import { CurrencyFormatPipe } from '../../../pipes/currency-format.pipe';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ConfirmDialogComponent,
    LoadingSpinnerComponent,
    GradeDisplayPipe,
    CurrencyFormatPipe
  ],
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.css']
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];
  filteredEmployees: Employee[] = [];
  selectedGrade = '';
  grades = Object.values(Grade);
  loading = true;
  showDeleteConfirm = false;
  selectedEmployee: Employee | null = null;

  constructor(
    private employeeService: EmployeeService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.loading = true;
    this.employeeService.getAllEmployees().subscribe({
      next: (employees) => {
        this.employees = employees;
        this.filteredEmployees = employees;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading employees:', error);
        this.notificationService.error('Failed to load employees');
        this.loading = false;
      }
    });
  }

  filterByGrade(): void {
    if (this.selectedGrade) {
      this.filteredEmployees = this.employees.filter(emp => emp.grade === this.selectedGrade);
    } else {
      this.filteredEmployees = this.employees;
    }
  }

  editEmployee(employeeId: string): void {
    this.router.navigate(['/employees/edit', employeeId]);
  }

  deleteEmployee(employee: Employee): void {
    // Temporary fix: Use native confirm dialog
    if (confirm(`Are you sure you want to delete ${employee.name}?`)) {
      this.employeeService.deleteEmployee(employee.employeeId).subscribe({
        next: () => {
          this.notificationService.success('Employee deleted successfully');
          this.loadEmployees();
        },
        error: (error) => {
          console.error('Error deleting employee:', error);
          this.notificationService.error(error.error?.message || 'Failed to delete employee');
        }
      });
    }
  }

  // Keep these for when we fix the modal
  confirmDelete(): void {
    if (this.selectedEmployee) {
      this.employeeService.deleteEmployee(this.selectedEmployee.employeeId).subscribe({
        next: () => {
          this.notificationService.success('Employee deleted successfully');
          this.loadEmployees();
          this.showDeleteConfirm = false;
          this.selectedEmployee = null;
        },
        error: (error) => {
          console.error('Error deleting employee:', error);
          this.notificationService.error(error.error?.message || 'Failed to delete employee');
          this.showDeleteConfirm = false;
          this.selectedEmployee = null;
        }
      });
    }
  }

  cancelDelete(): void {
    this.showDeleteConfirm = false;
    this.selectedEmployee = null;
  }
}
