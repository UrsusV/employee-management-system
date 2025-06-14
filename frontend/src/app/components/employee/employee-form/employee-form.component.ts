import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { EmployeeService } from '../../../services/employee.service';
import { NotificationService } from '../../../services/notification.service';
import { Employee } from '../../../models/employee.model';
import { Grade, AccountType } from '../../../models/enums';
import { GradeDisplayPipe } from '../../../pipes/grade-display.pipe';

@Component({
  selector: 'app-employee-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    GradeDisplayPipe
  ],
  templateUrl: './employee-form.component.html',
  styleUrls: ['./employee-form.component.css']
})
export class EmployeeFormComponent implements OnInit {
  employeeForm!: FormGroup;
  isEditMode = false;
  isSubmitting = false;
  employeeId: string | null = null;
  grades = Object.values(Grade);
  accountTypes = Object.values(AccountType);
  gradeWarning = '';

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.checkEditMode();
  }

  initializeForm(): void {
    this.employeeForm = this.fb.group({
      employeeId: ['', [Validators.required, Validators.pattern('^[0-9]{4}$')]],
      name: ['', [Validators.required, Validators.maxLength(100)]],
      grade: ['', Validators.required],
      address: ['', Validators.required],
      mobileNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10,15}$')]],
      bankAccount: this.fb.group({
        accountType: ['', Validators.required],
        accountName: ['', [Validators.required, Validators.maxLength(100)]],
        accountNumber: ['', [Validators.required, Validators.maxLength(50)]],
        currentBalance: [0, [Validators.required, Validators.min(0)]],
        bankName: ['', [Validators.required, Validators.maxLength(100)]],
        branchName: ['', [Validators.required, Validators.maxLength(100)]]
      })
    });
  }

  checkEditMode(): void {
    this.employeeId = this.route.snapshot.paramMap.get('id');
    if (this.employeeId) {
      this.isEditMode = true;
      this.loadEmployee();
    }
  }

  loadEmployee(): void {
    if (this.employeeId) {
      this.employeeService.getEmployee(this.employeeId).subscribe({
        next: (employee) => {
          this.employeeForm.patchValue(employee);
        },
        error: (error) => {
          this.notificationService.error('Failed to load employee');
          this.router.navigate(['/employees']);
        }
      });
    }
  }

  checkGradeAvailability(): void {
    const selectedGrade = this.employeeForm.get('grade')?.value;
    if (selectedGrade && !this.isEditMode) {
      this.employeeService.canAddEmployeeToGrade(selectedGrade).subscribe({
        next: (canAdd) => {
          this.gradeWarning = canAdd ? '' : 'This grade has reached maximum capacity';
        }
      });
    }
  }

  onSubmit(): void {
    if (this.employeeForm.valid) {
      this.isSubmitting = true;
      const employee: Employee = this.employeeForm.value;

      const operation = this.isEditMode
        ? this.employeeService.updateEmployee(this.employeeId!, employee)
        : this.employeeService.createEmployee(employee);

      operation.subscribe({
        next: () => {
          this.notificationService.success(`Employee ${this.isEditMode ? 'updated' : 'created'} successfully`);
          this.router.navigate(['/employees']);
        },
        error: (error) => {
          this.notificationService.error(error.error?.message || 'Operation failed');
          this.isSubmitting = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/employees']);
  }
}
