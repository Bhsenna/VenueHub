import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { RegisterRequest } from '../interfaces/register-request';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private apiUrl = 'http://localhost:8080/api/v1/auth';

  constructor(private httpClient: HttpClient) { }

  login(login: string, senha: string): Observable<{ token: string }> {
    return this.httpClient.post<{ token: string }>(`${this.apiUrl}/login`, { login, senha }).pipe(
      tap((response: { token: string }) => {
        localStorage.setItem('token', response.token);
      }
      ))
  }


  register(registerData: RegisterRequest) {
    return this.httpClient.post(`${this.apiUrl}/register`, registerData);
  }

  logout() {
    localStorage.removeItem('token');
  }
}
