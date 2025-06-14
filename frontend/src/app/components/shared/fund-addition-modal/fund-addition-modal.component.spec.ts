import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FundAdditionModalComponent } from './fund-addition-modal.component';

describe('FundAdditionModalComponent', () => {
  let component: FundAdditionModalComponent;
  let fixture: ComponentFixture<FundAdditionModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FundAdditionModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FundAdditionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
