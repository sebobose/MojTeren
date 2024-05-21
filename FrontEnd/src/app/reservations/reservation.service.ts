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

  getReservations(
    sport: string | null,
    sportCenterId: string | null,
    date: Date,
  ) {
    console.log(date.toISOString().split('T')[0]);
    return this.http.get(
      environment.BASE_API_URL +
        '/reservations/sport-center/' +
        sport +
        '/' +
        sportCenterId +
        '/' +
        date.toISOString().split('T')[0],
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
}
