import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterModule } from '@angular/router';
import { AuthenticationService } from '../../../services/authentication-service';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterModule
  ],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {

  registerForm: FormGroup;

  constructor(
    private router: Router,
    private fb: FormBuilder,
    private authService: AuthenticationService
  ) {
    this.registerForm = this.fb.group({
      nome: ['', Validators.required],
      sobrenome: ['', Validators.required],
      login: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(8)]],
      role: ['CLIENT'], 
      ativo: [true],    
      address: this.fb.group({
        cep: ['', Validators.required],
        logradouro: ['', Validators.required],
        numero: [null, Validators.required],
        complemento: [''],
        bairro: ['', Validators.required],
        cidade: ['', Validators.required],
        estado: ['', Validators.required],
        latitude: [1],
        longitude: [1]
      })
    });
  }

  onSubmit() {
    if (this.registerForm.invalid) return;

    const registerData = this.registerForm.value;

    this.authService.register(registerData).subscribe({
      next: () => {         
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.error('Falha no registro', err);
      }
    });
  }
}
