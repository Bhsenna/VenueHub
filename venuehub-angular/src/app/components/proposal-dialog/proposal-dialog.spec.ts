import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProposalDialog } from './proposal-dialog';

describe('ProposalDialog', () => {
  let component: ProposalDialog;
  let fixture: ComponentFixture<ProposalDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProposalDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProposalDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
