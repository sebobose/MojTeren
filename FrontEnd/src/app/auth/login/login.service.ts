import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private http = inject(HttpClient);
  login(loginForm: any) {
    return this.http.post(environment.BASE_API_URL + '/auth/login', loginForm);
  }
}
