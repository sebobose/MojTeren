import {Injectable} from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    constructor() { }

    ngOnInit() {
    }

    getUserRole() {
      const user = localStorage.getItem('user');
      if (user) {
        return user;
      }
      return null;
    }

  isUserLoggedIn() {
    return !!localStorage.getItem('token');
  }
}
