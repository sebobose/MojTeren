import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class SportCenterService {
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

  addSportCenter(sportCenter: any) {
    let header = new HttpHeaders({
      Authorization: 'Bearer ' + localStorage.getItem('token'),
    });
    let headersObj = {
      headers: header,
    };
    return this.http.post(
      environment.BASE_API_URL + '/sport-center/add',
      sportCenter,
      headersObj,
    );
  }

  getSportCenterById(sportCenterId: any) {
    return this.http.get(
      environment.BASE_API_URL + '/sport-center/' + sportCenterId,
      this.createHeader(),
    );
  }

  editSportCenter(sportCenter: any, id: any) {
    let header = new HttpHeaders({
      Authorization: 'Bearer ' + localStorage.getItem('token'),
    });
    let headersObj = {
      headers: header,
    };
    return this.http.put(
      environment.BASE_API_URL + '/sport-center/update/' + id,
      sportCenter,
      headersObj,
    );
  }

  deleteSportCenter(sportCenterId: any, reason: string) {
    return this.http.put(
      environment.BASE_API_URL + '/sport-center/deactivate/' + sportCenterId,
      reason,
      this.createHeader(),
    );
  }

  getSportCenterFields(sportCenterId: any) {
    return this.http.get(
      environment.BASE_API_URL + '/sport-center/fields/' + sportCenterId,
      this.createHeader(),
    );
  }

  getPosition(address: any) {
    return this.http.get(
      'https://maps.googleapis.com/maps/api/geocode/json?address=' +
        address +
        '&key=' +
        environment.GOOGLE_MAPS_API_KEY,
    );
  }

  getSportCenters() {
    return this.http.get(
      environment.BASE_API_URL + '/sport-center/user/all',
      this.createHeader(),
    );
  }
}
