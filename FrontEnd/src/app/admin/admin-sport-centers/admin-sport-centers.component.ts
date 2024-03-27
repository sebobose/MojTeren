import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-sport-centers',
  templateUrl: './admin-sport-centers.component.html',
  styleUrl: './admin-sport-centers.component.css',
})
export class AdminSportCentersComponent {
  private router = inject(Router);

  addSportCenter() {
    this.router.navigate(['/add-sport-center']).then(() => {
      window.location.reload();
    });
  }
}
