import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  private router = inject(Router);
  title = 'MojTeren';
  role = localStorage.getItem('role');

  changePage(page: string) {
    this.router.navigate([page]);
  }
}
