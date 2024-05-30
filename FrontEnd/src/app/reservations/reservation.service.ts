import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private http = inject(HttpClient);

  createHeader() {
    let header = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + localStorage.getItem('token'),
    });
    return {
      headers: header,
    };
  }

  getReservations(date: Date, fieldId: string | null) {
    return this.http.get(
      environment.BASE_API_URL +
        '/reservations/by-week/' +
        date.toISOString().split('T')[0] +
        '/' +
        fieldId,
    );
  }

  getSportCenter(sportCenterId: string | null, sport: string | null) {
    return this.http.get(
      environment.BASE_API_URL +
        '/reservations/sport-center/' +
        sport +
        '/' +
        sportCenterId,
    );
  }

  makeReservation(data: any) {
    return this.http.post(
      environment.BASE_API_URL + '/reservations/add',
      data,
      this.createHeader(),
    );
  }
}
