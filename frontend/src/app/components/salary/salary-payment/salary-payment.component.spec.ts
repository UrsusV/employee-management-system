import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalaryPaymentComponent } from './salary-payment.component';

describe('SalaryPaymentComponent', () => {
  let component: SalaryPaymentComponent;
  let fixture: ComponentFixture<SalaryPaymentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SalaryPaymentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SalaryPaymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
