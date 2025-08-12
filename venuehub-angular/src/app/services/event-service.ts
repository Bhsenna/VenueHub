import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private apiUrl = 'http://localhost:8080/api/v1/events';

  constructor(private http: HttpClient) { }

  getUserEvents(): Observable<any> {
    return this.http.get<any[]>(`${this.apiUrl}/all`);
  }

  getEventDetails(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/details`);
  }
}
