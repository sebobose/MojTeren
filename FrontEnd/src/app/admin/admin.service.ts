import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);

  getUsers() {
    return this.http.get(
      environment.BASE_API_URL + '/user/admin/users',
      this.createHeader(),
    );
  }

  getSportCenterRequests() {
    return this.http.get(
      environment.BASE_API_URL + '/user/admin/sport-center-requests',
      this.createHeader(),
    );
  }

  getAdminSports() {
    return this.http.get(
      environment.BASE_API_URL + '/sport/admin/all',
      this.createHeader(),
    );
  }

  addSport(sportName: string) {
    return this.http.post(
      environment.BASE_API_URL + '/sport/admin/add',
      sportName,
      this.createHeader(),
    );
  }

  deleteSport(sportName: string) {
    return this.http.delete(
      environment.BASE_API_URL + '/sport/admin/delete/' + sportName,
      this.createHeader(),
    );
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

  getAdminSportCenters() {
    return this.http.get(
      environment.BASE_API_URL + '/sport-center/admin/all',
      this.createHeader(),
    );
  }
}
