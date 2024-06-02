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
    let url =
      environment.BASE_API_URL +
      '/reservations/by-week/' +
      date.toISOString().split('T')[0] +
      '/' +
      fieldId;
    if (localStorage.getItem('token') === null) {
      return this.http.get(url);
    }
    return this.http.get(url, this.createHeader());
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

  getUserReservations() {
    return this.http.get(
      environment.BASE_API_URL + '/reservations/user',
      this.createHeader(),
    );
  }

  cancelReservation(reservationId: any, reason: any) {
    return this.http.put(
      environment.BASE_API_URL + '/reservations/cancel/' + reservationId,
      reason,
      this.createHeader(),
    );
  }

  checkUser(user: string) {
    return this.http.post(
      environment.BASE_API_URL + '/reservations/check-user',
      user,
      this.createHeader(),
    );
  }

  getCanceledReservations() {
    return this.http.get(
      environment.BASE_API_URL + '/reservations/canceled',
      this.createHeader(),
    );
  }
}
