import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class FieldService {
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

  getSports() {
    return this.http.get(environment.BASE_API_URL + '/sport/all');
  }

  addField(field: FormData) {
    return this.http.post(
      environment.BASE_API_URL + '/field/add',
      field,
      this.createHeader(),
    );
  }
}
