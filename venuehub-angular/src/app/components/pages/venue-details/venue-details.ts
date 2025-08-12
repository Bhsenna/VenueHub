import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { VenueResponse } from '../../../interfaces/venue-response';
import { VenueService } from '../../../services/venue-service';
import { ProposalDialog } from '../../proposal-dialog/proposal-dialog';

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
