import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SalarySheet } from '../../../models/salary.model';
import { CurrencyFormatPipe } from '../../../pipes/currency-format.pipe';
import { GradeDisplayPipe } from '../../../pipes/grade-display.pipe';

@Component({
  selector: 'app-salary-sheet',
  standalone: true,
  imports: [CommonModule, CurrencyFormatPipe, GradeDisplayPipe],
  templateUrl: './salary-sheet.component.html',
  styleUrls: ['./salary-sheet.component.css']
})
export class SalarySheetComponent {
  @Input() salarySheet: SalarySheet | null = null;
}
