import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);

  getUsers() {
    return this.http.get(environment.BASE_API_URL + '/user/admin/users');
  }

  getSportCenterRequests() {
    return this.http.get(
      environment.BASE_API_URL + '/user/admin/sport-center-requests',
    );
  }
}
