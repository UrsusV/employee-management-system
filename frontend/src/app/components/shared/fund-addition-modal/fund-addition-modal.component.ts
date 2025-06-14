import { Component, Input, Output, EventEmitter, OnInit, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { FundAddition } from '../../../models/salary.model';
import { CurrencyFormatPipe } from '../../../pipes/currency-format.pipe';

@Component({
  selector: 'app-fund-addition-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CurrencyFormatPipe],
  templateUrl: './fund-addition-modal.component.html',
  styleUrls: ['./fund-addition-modal.component.css']
})
export class FundAdditionModalComponent implements OnInit, OnChanges {
  @Input() show = false;
  @Input() requiredAmount = 0;
  @Output() addFunds = new EventEmitter<FundAddition>();
  @Output() cancel = new EventEmitter<void>();

  fundForm!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.fundForm = this.fb.group({
      amount: ['', [Validators.required, Validators.min(0.01)]],
      description: ['Emergency fund addition for salary payment']
    });
  }

  ngOnChanges(): void {
    if (this.requiredAmount > 0 && this.fundForm) {
      this.fundForm.get('amount')?.setValidators([
        Validators.required,
        Validators.min(this.requiredAmount)
      ]);
      this.fundForm.get('amount')?.setValue(this.requiredAmount);
    }
  }

  onAddFunds(): void {
    if (this.fundForm.valid) {
      this.addFunds.emit(this.fundForm.value);
    }
  }

  onCancel(): void {
    this.cancel.emit();
    this.fundForm.reset();
  }
}
