import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AdditionalService {

  private apiUrl = 'http://localhost:8080/api/v1/additionals';

  constructor(private http: HttpClient) { }

    getAllAdditionals() {
    return this.http.get<any[]>(`${this.apiUrl}/all`);
  }
}
