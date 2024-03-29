import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);

  getUserRole() {
    const user = localStorage.getItem('role');
    if (user) {
      return user;
    }
    return null;
  }

  isUserLoggedIn() {
    return !!localStorage.getItem('token');
  }

  createHeader() {
    let header = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + localStorage.getItem('token'),
    });
    return {
      headers: header,
    };
  }

  checkTokenAvailability() {
    return this.http.get(
      environment.BASE_API_URL + '/user/check-token',
      this.createHeader(),
    );
  }
}
