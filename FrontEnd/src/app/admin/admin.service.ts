import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
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

  getUsers() {
    return this.http.get(
      environment.BASE_API_URL + '/user/admin/users',
      this.createHeader(),
    );
  }

  getSportCenterRequests() {
    return this.http.get(
      environment.BASE_API_URL + '/sport-center/admin/requests',
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

  resolveRequest(sportCenterId: any, reason: any) {
    return this.http.put(
      environment.BASE_API_URL + '/sport-center/admin/resolve',
      { sportCenterId: sportCenterId, decision: reason[0], reason: reason[1] },
      this.createHeader(),
    );
  }
}
