import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VenueCalendar } from './venue-calendar';

describe('VenueCalendar', () => {
  let component: VenueCalendar;
  let fixture: ComponentFixture<VenueCalendar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VenueCalendar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VenueCalendar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
