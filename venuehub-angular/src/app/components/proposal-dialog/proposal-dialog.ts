import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogContent, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-proposal-dialog',
  imports: [MatDialogModule, MatFormFieldModule, CommonModule],
  templateUrl: './proposal-dialog.html',
  styleUrl: './proposal-dialog.css'
})
export class ProposalDialog {

   proposalValue!: number;
  message!: string;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ProposalDialog>
  ) {}

  sendProposal() {
    console.log('Enviando proposta:', {
      venueId: this.data.venueId,
      value: this.proposalValue,
      message: this.message
    });
    this.dialogRef.close();
  }

}
