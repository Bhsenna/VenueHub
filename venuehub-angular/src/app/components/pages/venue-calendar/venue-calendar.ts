import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CalendarOptions } from '@fullcalendar/core';
import { VenueService } from '../../../services/venue-service';
import { EventResponse } from '../../../interfaces/event-response';
import { CommonModule } from '@angular/common';
import { FullCalendarModule } from '@fullcalendar/angular';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';

@Component({
  selector: 'app-venue-calendar',
  standalone: true,
  imports: [CommonModule, FullCalendarModule],
  templateUrl: './venue-calendar.html',
  styleUrls: ['./venue-calendar.css']
})
export class VenueCalendarComponent {
  venueId!: number;
  calendarOptions!: CalendarOptions;

  constructor(
    private route: ActivatedRoute,
    private venueService: VenueService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.venueId = +this.route.snapshot.paramMap.get('id')!;

    this.venueService.getAllEvents(this.venueId).subscribe(response => {
      const events = response.currentPageData.map((event: EventResponse) => ({
        id: event.id,
        title: event.nome,
        start: event.dataInicio,
        end: event.dataFim
      }));

      this.calendarOptions = {
        plugins: [dayGridPlugin, interactionPlugin],
        initialView: 'dayGridMonth',
        events: events,
        editable: false,
        selectable: true,
        eventClick: (clickInfo) => {
          const eventId = clickInfo.event.id; 
          this.router.navigate(['/events', eventId]);
        }
      };
    });
  }
}
