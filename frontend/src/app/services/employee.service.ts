import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Employee } from '../models/employee.model';
import { Grade } from '../models/enums';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService extends ApiService {
  private employeeUrl = `${this.baseUrl}/employees`;

  constructor(http: HttpClient) {
    super(http);
  }

  createEmployee(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.employeeUrl, employee)
      .pipe(catchError(this.handleError));
  }

  updateEmployee(employeeId: string, employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.employeeUrl}/${employeeId}`, employee)
      .pipe(catchError(this.handleError));
  }

  getEmployee(employeeId: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.employeeUrl}/${employeeId}`)
      .pipe(catchError(this.handleError));
  }

  getAllEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.employeeUrl)
      .pipe(catchError(this.handleError));
  }

  getEmployeesByGrade(grade: Grade): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.employeeUrl}/grade/${grade}`)
      .pipe(catchError(this.handleError));
  }

  deleteEmployee(employeeId: string): Observable<void> {
    return this.http.delete<void>(`${this.employeeUrl}/${employeeId}`)
      .pipe(catchError(this.handleError));
  }

  canAddEmployeeToGrade(grade: Grade): Observable<boolean> {
    return this.http.get<boolean>(`${this.employeeUrl}/grade/${grade}/can-add`)
      .pipe(catchError(this.handleError));
  }
}
