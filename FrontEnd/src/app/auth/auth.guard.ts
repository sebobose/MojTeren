import { inject, Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard {
  private authService = inject(AuthService);
  private router = inject(Router);

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const requiredRole = next.data['requiredRole'];
    if (!this.authService.isUserLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }
    const userRole = this.authService.getUserRole();
    return !!requiredRole.includes(userRole);
  }
}
