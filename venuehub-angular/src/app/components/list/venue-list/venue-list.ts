import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { VenueService } from '../../../services/venue-service';
import { VenueResponse } from '../../../interfaces/venue-response';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user-service'; // supondo que exista

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
  isOwner = false;

  constructor(
    private venueService: VenueService,
    private userService: UserService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.isOwner = user.role === 'OWNER';
        this.loadVenues();
      },
      error: (err) => {
        console.error('Erro ao buscar usuário atual:', err);
        this.loadVenues(); 
      }
    });
  }

  loadVenues(): void {
    this.isLoading = true;

    const venueObservable = this.isOwner
      ? this.venueService.getVenuesByOwner() 
      : this.venueService.getVenues();       

    venueObservable.subscribe({
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
