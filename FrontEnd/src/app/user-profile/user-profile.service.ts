import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserProfileService {
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

  getUserProfile() {
    return this.http.get(
      environment.BASE_API_URL + '/user/profile',
      this.createHeader(),
    );
  }

  editProfile(data: any) {
    return this.http.put(
      environment.BASE_API_URL + '/user/profile',
      data,
      this.createHeader(),
    );
  }
}
