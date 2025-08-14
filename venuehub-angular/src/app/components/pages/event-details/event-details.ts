import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { EventService } from '../../../services/event-service';
import { ProposalService } from '../../../services/proposal-service';
import { VenueService } from '../../../services/venue-service';
import { UserService } from '../../../services/user-service';

@Component({
  selector: 'app-event-details',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './event-details.html',
  styleUrl: './event-details.css'
})
export class EventDetails implements OnInit {
  event: any;
  proposals: any[] = [];
  isOwner = false;

  constructor(
    private route: ActivatedRoute,
    private eventService: EventService,
    private proposalService: ProposalService,
    private venueService: VenueService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.userService.getCurrentUser().subscribe(user => {
      this.isOwner = user.role === 'OWNER';
      this.loadEventAndProposals(id);
    });
  }

  private loadEventAndProposals(id: number) {
    this.eventService.getEventDetails(id).subscribe(event => {
      this.event = event;
    });

    const proposals$ = this.isOwner
      ? this.proposalService.getProposalsByVenue(id)
      : this.proposalService.getProposalsByEvent(id);

    proposals$.subscribe(async (response) => {
      const proposals = response.currentPageData || [];

      for (let proposal of proposals) {
        if (proposal.venueId) {
          const venue = await this.venueService.getVenueById(proposal.venueId).toPromise();
          proposal.venueName = venue?.nome || 'Nome não encontrado';
        }
      }

      this.proposals = proposals;
    });
  }
}
