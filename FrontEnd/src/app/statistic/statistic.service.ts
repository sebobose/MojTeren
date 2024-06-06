import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class StatisticService {
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

  getSportCentersAndFields() {
    return this.http.get(
      environment.BASE_API_URL + '/user/field-owner/statistics',
      this.createHeader(),
    );
  }

  getStatistics(data: any) {
    return this.http.post(
      environment.BASE_API_URL + '/user/field-owner/statistics',
      data,
      this.createHeader(),
    );
  }
}
