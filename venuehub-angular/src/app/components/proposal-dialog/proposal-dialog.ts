import { CommonModule } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { ProposalService } from '../../services/proposal-service';
import { EventService } from '../../services/event-service';
import { MatButtonModule } from '@angular/material/button';


@Component({
  selector: 'app-proposal-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './proposal-dialog.html',
  styleUrl: './proposal-dialog.css'
})
export class ProposalDialog implements OnInit {

  userEvents: any[] = [];
  selectedEventId!: number;
  proposalValue!: number;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ProposalDialog>,
    private eventService: EventService,
    private proposalService: ProposalService
  ) {}

  ngOnInit() {
    this.eventService.getUserEvents().subscribe({
      next: (events) => this.userEvents = events.currentPageData,
      error: (err) => console.error('Erro ao buscar eventos:', err)
    });
  }

  sendProposal() {
    const payload = {
      eventId: this.selectedEventId,
      venueId: this.data.venueId,
      valor: this.proposalValue
    };

    this.proposalService.createProposal(payload).subscribe({
    next: (res) => {
      console.log('Proposta criada com sucesso', res);
      this.dialogRef.close(res); 
    },
    error: (err) => {
      console.error('Erro ao criar proposta', err);
    }
  });
  
    this.dialogRef.close(payload);
  }
}
