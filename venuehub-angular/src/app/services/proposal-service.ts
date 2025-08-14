import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProposalService {
  private apiUrl = 'http://localhost:8080/api/v1/proposals';

  constructor(private http: HttpClient) { }

  createProposal(request: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/create`, request);
  }

  getAllProposals() {
    return this.http.get<any[]>(`${this.apiUrl}/all`);
  }

  getProposalsByEvent(eventId: number): Observable<any> {
    return this.http.get<any[]>(`${this.apiUrl}/event/${eventId}`);
  }

    getProposalsByVenue(venueId: number): Observable<any> {
    return this.http.get<any[]>(`${this.apiUrl}/venues/${venueId}`);
  }

  acceptProposal(id: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/aceita/${id}`, {});
  }

  rejectProposal(id: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/rejeita/${id}`, {});
  }
}
