import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  getUserRole() {
    const user = localStorage.getItem('role');
    if (user) {
      return user;
    }
    return null;
  }

  isUserLoggedIn() {
    return !!localStorage.getItem('token');
  }
}
