import { Component } from '@angular/core';
import { AuthenticationService } from '../../services/authentication-service';
import { Form, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-login',
  imports: [
     ReactiveFormsModule,
  CommonModule,
  MatFormFieldModule,
  MatInputModule,
  MatButtonModule,
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  loginForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private authenticationService: AuthenticationService) {
    this.loginForm = this.formBuilder.group({
      login: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required]]
    });
  }

  onSubmit(login: string, senha: string) {

    if (this.loginForm.invalid) {
      return;
    }

    this.authenticationService.login(login, senha).subscribe({
      next: (response) => {
        console.log('Login successful', response);
      },
      error: (error) => {
        console.error('Login failed', error);
      }

    });
  }
}
