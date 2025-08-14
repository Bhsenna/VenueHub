import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class CepService {
    private readonly apiUrl = 'https://viacep.com.br/ws/';

    constructor(private http: HttpClient) {}

    getCepData(cep: string) {
      return this.http.get(`${this.apiUrl}${cep}/json/`);
    }
}