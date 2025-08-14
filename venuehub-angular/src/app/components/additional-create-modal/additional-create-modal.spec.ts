import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdditionalCreateModal } from './additional-create-modal';

describe('AdditionalCreateModal', () => {
  let component: AdditionalCreateModal;
  let fixture: ComponentFixture<AdditionalCreateModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdditionalCreateModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdditionalCreateModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
