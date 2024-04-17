import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class HomepageService {
  private http = inject(HttpClient);

  getSports() {
    return this.http.get(environment.BASE_API_URL + '/sport/all');
  }

  getSportCenters(form: any) {
    return this.http.post(environment.BASE_API_URL + '/sport-center/all', form);
  }
}
