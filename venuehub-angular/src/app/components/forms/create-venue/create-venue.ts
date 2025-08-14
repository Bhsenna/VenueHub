import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { VenueService } from '../../../services/venue-service';
import { CepService } from '../../../services/cep-service';
import { Router, RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-create-venue',
  templateUrl: './create-venue.html',
  imports: [
    ReactiveFormsModule,
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterModule,
    MatCardModule
  ],
  styleUrls: ['./create-venue.css']
})
export class CreateVenue {

  venueForm: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private venueService: VenueService,
    private cepService: CepService,
    private router: Router
  ) {
    this.venueForm = this.fb.group({
      nome: ['', Validators.required],
      capacidade: [null, [Validators.required, Validators.min(1)]],
      descricao: [''],
      telefone: ['', [Validators.required, Validators.pattern(/^\(?[1-9]{2}\)? ?(?:[2-8]|9[1-9])[0-9]{3}-?[0-9]{4}$/)]],
      valor: [0, [Validators.required, Validators.min(0)]],
      address: this.fb.group({
        cep: ['', Validators.required],
        logradouro: ['', Validators.required],
        numero: [null, Validators.required],
        complemento: [''],
        bairro: ['', Validators.required],
        cidade: ['', Validators.required],
        estado: ['', Validators.required],
        latitude: [0],
        longitude: [0]
      })
    });
  }

  onCepChange(event: KeyboardEvent) {
    let cep = (event.target as HTMLInputElement).value?.replace(/\D/g, '');
    let validacep = /^[0-9]{8}$/;
    if (validacep.test(cep)) {
      let log = this.venueForm.get("address")?.get("logradouro");
      let bai = this.venueForm.get("address")?.get("bairro");
      let cid = this.venueForm.get("address")?.get("cidade");
      let est = this.venueForm.get("address")?.get("estado");
      log?.setValue("...");
      bai?.setValue("...");
      cid?.setValue("...");
      est?.setValue("...");

      this.cepService.getCepData(cep).subscribe(
        {
          next: (v: any) => {
            log?.setValue(v.logradouro);
            bai?.setValue(v.bairro);
            cid?.setValue(v.localidade);
            est?.setValue(v.uf);
          },
          error: (e) => console.log(e)
        }
      );
    }
  }

  submit() {
    if (this.venueForm.invalid) {
      this.venueForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    this.venueService.createVenue(this.venueForm.value).subscribe({
      next: (res) => {
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.error('Erro ao criar venue:', err);
        this.isSubmitting = false;
      }
    });
  }
}
