import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { VenueResponse } from '../interfaces/venue-response';
import { Observable } from 'rxjs';


@Injectable({
    providedIn: 'root'
})
export class VenueService {

    private apiUrl = 'http://localhost:8080/api/v1/venues';

    constructor(private httpClient: HttpClient) { }

    getVenues(): Observable<any> {
        return this.httpClient.get<any>(this.apiUrl);
    }

    getVenuesByOwner(): Observable<any> {
        return this.httpClient.get<any>(`${this.apiUrl}/all`);
    }

    getEvents(venueId: number, month: number, year: number) {
        return this.httpClient.get<any>(
            `${this.apiUrl}/events?venueId=${venueId}&month=${month}&year=${year}`
        );
    }

    getAllEvents(venueId: number) {
        return this.httpClient.get<any>(`${this.apiUrl}/all-events?venueId=${venueId}`);
    }



    getVenueById(id: string) {
        return this.httpClient.get<VenueResponse>(`${this.apiUrl}/${id}`);
    }

    createVenue(venue: VenueResponse) {
        return this.httpClient.post<VenueResponse>(this.apiUrl, venue);
    }

    updateVenue(id: string, venue: VenueResponse) {
        return this.httpClient.put<VenueResponse>(`${this.apiUrl}/${id}`, venue);
    }

    deleteVenue(id: string) {
        return this.httpClient.delete(`${this.apiUrl}/${id}`);
    }

}
