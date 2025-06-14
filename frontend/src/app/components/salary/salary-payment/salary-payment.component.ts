import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SalaryService } from '../../../services/salary.service';
import { CompanyService } from '../../../services/company.service';
import { NotificationService } from '../../../services/notification.service';
import { SalarySheet, FundAddition } from '../../../models/salary.model';
import { SalarySheetComponent } from '../salary-sheet/salary-sheet.component';
import { LoadingSpinnerComponent } from '../../shared/loading-spinner/loading-spinner.component';
import { FundAdditionModalComponent } from '../../shared/fund-addition-modal/fund-addition-modal.component';
import { CurrencyFormatPipe } from '../../../pipes/currency-format.pipe';

@Component({
  selector: 'app-salary-payment',
  standalone: true,
  imports: [
    CommonModule,
    SalarySheetComponent,
    LoadingSpinnerComponent,
    FundAdditionModalComponent,
    CurrencyFormatPipe
  ],
  templateUrl: './salary-payment.component.html',
  styleUrls: ['./salary-payment.component.css']
})
export class SalaryPaymentComponent implements OnInit {
  companyBalance = 0;
  totalSalaryRequired = 0;
  salarySheet: SalarySheet | null = null;
  loading = false;
  showFundModal = false;
  requiredFundAmount = 0;

  constructor(
    private salaryService: SalaryService,
    private companyService: CompanyService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadCompanyBalance();
  }

  loadCompanyBalance(): void {
    this.companyService.getCompanyBalance().subscribe({
      next: (balance) => {
        this.companyBalance = balance;
      },
      error: (error) => {
        this.notificationService.error('Failed to load company balance');
      }
    });
  }

  previewSalarySheet(): void {
    this.loading = true;
    this.salaryService.generateSalarySheet().subscribe({
      next: (sheet) => {
        this.salarySheet = sheet;
        this.totalSalaryRequired = sheet.salaryDetails.reduce((sum, detail) => sum + detail.totalSalary, 0);
        this.loading = false;
      },
      error: (error) => {
        this.notificationService.error('Failed to generate salary sheet');
        this.loading = false;
      }
    });
  }

  processSalaryPayment(): void {
    this.loading = true;
    this.salaryService.processSalaryPayment().subscribe({
      next: (result) => {
        if (result.error === 'INSUFFICIENT_FUNDS') {
          this.handleInsufficientFunds(result);
        } else {
          this.handleSuccessfulPayment(result);
        }
        this.loading = false;
      },
      error: (error) => {
        if (error.status === 402) {
          this.handleInsufficientFunds(error.error);
        } else {
          this.notificationService.error('Failed to process salary payment');
        }
        this.loading = false;
      }
    });
  }

  private handleInsufficientFunds(error: any): void {
    this.requiredFundAmount = this.totalSalaryRequired - this.companyBalance;
    this.showFundModal = true;
    this.notificationService.warning(error.message);
  }

  private handleSuccessfulPayment(salarySheet: SalarySheet): void {
    this.salarySheet = salarySheet;
    this.notificationService.success('Salary payment processed successfully!');
    this.loadCompanyBalance();
  }

  onAddFunds(fundAddition: FundAddition): void {
    this.loading = true;
    this.salaryService.processSalaryPaymentWithFunds(fundAddition).subscribe({
      next: (salarySheet) => {
        this.salarySheet = salarySheet;
        this.showFundModal = false;
        this.notificationService.success('Funds added and salary payment processed successfully!');
        this.loadCompanyBalance();
        this.loading = false;
      },
      error: (error) => {
        this.notificationService.error('Failed to process payment');
        this.loading = false;
      }
    });
  }
}
