// user-service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';

  constructor(private http: HttpClient) {}

  getCurrentUser(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }
}
