import { Component, inject, OnInit } from '@angular/core';
import { HomepageService } from './homepage.service';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent implements OnInit {
  private homepageService = inject(HomepageService);
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);

  activeSport: any = 'Nogomet';
  sports: any;
  filterForm = this.formBuilder.nonNullable.group(
    {
      distanceChange: [10],
      date: [''],
      timeChangeLow: [300],
      timeChangeHigh: [1439],
    },
    {
      updateOn: 'change',
    },
  );
  minDate: any = new Date();

  ngOnInit(): void {
    if (localStorage.getItem('role') === 'ADMIN') {
      this.router.navigate(['/admin/requests']).then(() => {
        window.location.reload();
      });
    } else if (localStorage.getItem('role') === 'FIELD_OWNER') {
      this.router.navigate(['/field-owner/home']).then(() => {
        window.location.reload();
      });
    }
    this.homepageService.getSports().subscribe({
      next: (data) => {
        this.sports = data;
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
  }

  getSport(sport: any) {
    this.activeSport = sport.name;
  }

  doSearch() {}

  applyFilters() {}

  convertMinutesToTime(minutes: number): string {
    let hours = Math.floor(minutes / 60);
    let mins = minutes % 60;
    return `${hours}:${mins < 10 ? '0' : ''}${mins}`;
  }
}
