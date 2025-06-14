import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Company } from '../models/company.model';
import { FundAddition } from '../models/salary.model';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class CompanyService extends ApiService {
  private companyUrl = `${this.baseUrl}/company`;

  constructor(http: HttpClient) {
    super(http);
  }

  getCompany(): Observable<Company> {
    return this.http.get<Company>(this.companyUrl)
      .pipe(catchError(this.handleError));
  }

  updateCompany(company: Company): Observable<Company> {
    return this.http.put<Company>(this.companyUrl, company)
      .pipe(catchError(this.handleError));
  }

  getCompanyBalance(): Observable<number> {
    return this.http.get<{ balance: number }>(`${this.companyUrl}/balance`)
      .pipe(
        map(response => response.balance),
        catchError(this.handleError)
      );
  }

  addFunds(fundAddition: FundAddition): Observable<any> {
    return this.http.post(`${this.companyUrl}/add-funds`, fundAddition)
      .pipe(catchError(this.handleError));
  }
}
