import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { ProposalService } from '../../../services/proposal-service';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-proposal-list',
  standalone: true,
   imports: [MatCardModule, MatButtonModule, MatIconModule, CommonModule],
  templateUrl: './proposal-list.html',
  styleUrls: ['./proposal-list.css']
})
export class ProposalList {
  proposals: any[] = [];
  isLoading = false;

  constructor(private proposalService: ProposalService) {}

  ngOnInit(): void {
    this.loadProposals();
  }

  loadProposals(): void {
    this.isLoading = true;
    this.proposalService.getAllProposals().subscribe({
      next: (data: any) => {
        this.proposals = data.currentPageData;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar propostas', err);
        this.isLoading = false;
      }
    });
  }

  acceptProposal(id: number) {
    this.proposalService.acceptProposal(id).subscribe({
      next: () => {
        this.loadProposals(); 
      },
      error: (err) => console.error('Erro ao aceitar proposta', err)
    });
  }

  rejectProposal(id: number) {
    this.proposalService.rejectProposal(id).subscribe({
      next: () => {
        this.loadProposals(); 
      },
      error: (err) => console.error('Erro ao recusar proposta', err)
    });
  }
}
