import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class RegisterService {
  private http = inject(HttpClient);

  register(registerForm: any) {
    console.log(registerForm);
    return this.http.post(
      environment.BASE_API_URL + '/auth/register',
      registerForm,
    );
  }
}
