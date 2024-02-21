import { inject, Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthService } from './auth.service';
// import {HeaderService} from "../header/header.service";

@Injectable({
  providedIn: 'root',
})
export class AuthLoginGuard {
  private authService = inject(AuthService);
  private router = inject(Router);

  canActivate() {
    if (this.authService.isUserLoggedIn()) {
      alert(
        'Vec ste prijavljeni, za ponovnu prijavu ili registraciju potrebna je odjava!',
      );
      // this.router.navigate(['']);
      // const userRole = this.authService.getUserRole();
    }
    return true;
  }
}
