import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SalaryCalculation, SalarySheet, FundAddition } from '../models/salary.model';
import { Grade } from '../models/enums';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class SalaryService extends ApiService {
  private salaryUrl = `${this.baseUrl}/salary`;

  constructor(http: HttpClient) {
    super(http);
  }

  calculateEmployeeSalary(employeeId: string): Observable<SalaryCalculation> {
    return this.http.get<SalaryCalculation>(`${this.salaryUrl}/calculate/${employeeId}`)
      .pipe(catchError(this.handleError));
  }

  calculateAllSalaries(): Observable<SalaryCalculation[]> {
    return this.http.get<SalaryCalculation[]>(`${this.salaryUrl}/calculate-all`)
      .pipe(catchError(this.handleError));
  }

  getBasicSalary(grade: Grade): Observable<{ grade: string; basicSalary: number }> {
    return this.http.get<{ grade: string; basicSalary: number }>(`${this.salaryUrl}/basic-salary/${grade}`)
      .pipe(catchError(this.handleError));
  }

  processSalaryPayment(): Observable<SalarySheet | any> {
    return this.http.post<SalarySheet>(`${this.salaryUrl}/process-payment`, {})
      .pipe(catchError(this.handleError));
  }

  processSalaryPaymentWithFunds(fundAddition: FundAddition): Observable<SalarySheet> {
    return this.http.post<SalarySheet>(`${this.salaryUrl}/process-payment-with-funds`, fundAddition)
      .pipe(catchError(this.handleError));
  }

  generateSalarySheet(): Observable<SalarySheet> {
    return this.http.get<SalarySheet>(`${this.salaryUrl}/sheet`)
      .pipe(catchError(this.handleError));
  }
}
