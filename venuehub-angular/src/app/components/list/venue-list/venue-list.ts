import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { VenueService } from '../../../services/venue-service';
import { VenueResponse } from '../../../interfaces/venue-response';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-venue-list',
  imports: [
    MatCardModule,
    CommonModule
  ],
  templateUrl: './venue-list.html',
  styleUrl: './venue-list.css'
})
export class VenueList {

  venues: VenueResponse[] = [];
  isLoading = false;

  constructor(private venueService: VenueService, private router: Router) { }

  ngOnInit(): void {
    this.loadVenues();
  }

  loadVenues(): void {
    this.isLoading = true;
    this.venueService.getVenues().subscribe({
      next: (data) => {
        this.venues = data.currentPageData;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar venues:', err);
        this.isLoading = false;
      }
    });
  }

  goToDetails(id: number) {
    this.router.navigate(['/venues', id]);
  }

}
