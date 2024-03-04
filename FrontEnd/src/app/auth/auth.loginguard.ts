import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

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
      switch (this.authService.getUserRole()) {
        case 'ADMIN':
          this.router.navigate(['/admin/requests']).then(() => {
            window.location.reload();
          });
          break;
        case 'FIELD_OWNER':
          this.router.navigate(['/field-owner/home']).then(() => {
            window.location.reload();
          });
          break;
        case 'ATHLETE':
          this.router.navigate(['/home']).then(() => {
            window.location.reload();
          });
          break;
        default:
          break;
      }
    }
    return true;
  }
}
