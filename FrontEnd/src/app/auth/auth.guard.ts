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
    if (!this.authService.isUserLoggedIn()) {
      this.router.navigate(['/home']).then(() => {
        window.location.reload();
      });
      return false;
    }
    const requiredRole = next.data['requiredRole'];
    const userRole = this.authService.getUserRole();
    if (requiredRole.includes(userRole)) {
      return true;
    } else {
      switch (userRole) {
        case 'ADMIN':
          this.router.navigate(['/admin/requests']).then(() => {
            window.location.reload();
          });
          break;
        case 'FIELD_OWNER':
          this.router.navigate(['/field-owner/sport-centers']).then(() => {
            window.location.reload();
          });
          break;
        case 'ATHLETE':
          this.router.navigate(['/home']).then(() => {
            window.location.reload();
          });
          break;
        default:
          return false;
      }
    }
    return true;
  }
}
