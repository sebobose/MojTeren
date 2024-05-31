import { Component, inject, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  private router = inject(Router);
  role = localStorage.getItem('role');
  active: any;

  ngOnInit() {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.active = this.router.url;
      }
    });
  }

  changePage(page: string) {
    this.router.navigate([page]).then(() => window.location.reload());
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']).then(() => window.location.reload());
  }

  myReservations() {
    this.router
      .navigate(['/user-reservations'])
      .then(() => window.location.reload());
  }
}
