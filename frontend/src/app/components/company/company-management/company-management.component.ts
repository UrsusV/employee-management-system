import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CompanyService } from '../../../services/company.service';
import { NotificationService } from '../../../services/notification.service';
import { Company } from '../../../models/company.model';
import { CurrencyFormatPipe } from '../../../pipes/currency-format.pipe';

@Component({
  selector: 'app-company-management',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CurrencyFormatPipe
  ],
  templateUrl: './company-management.component.html',
  styleUrls: ['./company-management.component.css']
})
export class CompanyManagementComponent implements OnInit {
  company: Company | null = null;
  fundForm!: FormGroup;
  salaryForm!: FormGroup;
  isSubmitting = false;
  isUpdating = false;

  constructor(
    private fb: FormBuilder,
    private companyService: CompanyService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.initializeForms();
    this.loadCompanyDetails();
  }

  initializeForms(): void {
    this.fundForm = this.fb.group({
      amount: ['', [Validators.required, Validators.min(0.01)]],
      description: ['']
    });

    this.salaryForm = this.fb.group({
      baseSalaryLowestGrade: ['', [Validators.required, Validators.min(0)]]
    });
  }

  loadCompanyDetails(): void {
    this.companyService.getCompany().subscribe({
      next: (company) => {
        this.company = company;
        this.salaryForm.patchValue({
          baseSalaryLowestGrade: company.baseSalaryLowestGrade
        });
      },
      error: (error) => {
        this.notificationService.error('Failed to load company details');
      }
    });
  }

  addFunds(): void {
    if (this.fundForm.valid) {
      this.isSubmitting = true;
      this.companyService.addFunds(this.fundForm.value).subscribe({
        next: () => {
          this.notificationService.success('Funds added successfully');
          this.fundForm.reset();
          this.loadCompanyDetails();
          this.isSubmitting = false;
        },
        error: (error) => {
          this.notificationService.error('Failed to add funds');
          this.isSubmitting = false;
        }
      });
    }
  }

  updateBaseSalary(): void {
    if (this.salaryForm.valid && this.company) {
      this.isUpdating = true;
      const updatedCompany: Company = {
        ...this.company,
        baseSalaryLowestGrade: this.salaryForm.value.baseSalaryLowestGrade
      };

      this.companyService.updateCompany(updatedCompany).subscribe({
        next: () => {
          this.notificationService.success('Base salary updated successfully');
          this.loadCompanyDetails();
          this.isUpdating = false;
        },
        error: (error) => {
          this.notificationService.error('Failed to update base salary');
          this.isUpdating = false;
        }
      });
    }
  }
}
