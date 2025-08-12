import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { EventService } from '../../../services/event-service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule],
  templateUrl: './event-list.html',
  styleUrl: './event-list.css'
})
export class EventList {
  events: any[] = [];

  constructor(private eventService: EventService, private router: Router) {}

  ngOnInit(): void {
    this.eventService.getUserEvents().subscribe({
      next: (res) => {
        this.events = res.currentPageData || [];
      },
      error: (err) => {
        console.error('Erro ao carregar eventos', err);
      }
    });
  }

   goToDetails(id: number) {
    this.router.navigate(['/events', id]);
  }
}
