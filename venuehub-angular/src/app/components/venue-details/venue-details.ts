import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VenueService } from '../../services/venue-service';
import { MatDialog } from '@angular/material/dialog';
import { VenueResponse } from '../../interfaces/venue-response';
import { MatCardModule } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { ProposalDialog } from '../proposal-dialog/proposal-dialog';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-venue-details',
  imports: [MatCardModule, MatIcon, CommonModule],
  templateUrl: './venue-details.html',
  styleUrl: './venue-details.css'
})
export class VenueDetails {
  venue!: VenueResponse;

  constructor(
    private route: ActivatedRoute,
    private venueService: VenueService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.venueService.getVenueById(id).subscribe((data) => {
      this.venue = data ;
    });
  }

  openProposalDialog() {
    this.dialog.open(ProposalDialog, {
      width: '400px',
      data: { venueId: this.venue.id, venueName: this.venue.nome }
    });
  }
}
