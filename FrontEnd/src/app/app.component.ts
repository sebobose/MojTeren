import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from './auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  ngOnInit() {
    if (this.authService.isUserLoggedIn()) {
      this.authService.checkTokenAvailability().subscribe({
        next: () => {
          return;
        },
        error: () => {
          alert('Vaša sesija je istekla, za nastavak potrebna je prijava.');
          localStorage.clear();
          this.router.navigate(['/login']).then(() => {
            window.location.reload();
          });
        },
      });
    }
  }
}
