import { Component, inject, OnInit } from '@angular/core';
import { HomepageService } from './homepage.service';
import { FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent implements OnInit {
  private homepageService = inject(HomepageService);
  private formBuilder = inject(FormBuilder);

  activeSport: any = 'Nogomet';
  sports: any = [
    'Nogomet',
    'Košarka',
    'Rukomet',
    'Tenis',
    'Stolni tenis',
    'Badminton',
    'Odbojka',
    '??‍♂️ Plivanje',
    '??‍♂️ Skijanje',
    '??‍♂️ Snowboarding',
    '??‍♂️ Planinarenje',
    '??‍♂️ Trčanje',
    '??‍♂️ Biciklizam',
    '??‍♂️ Rolanje',
    '??‍♂️ Jahanje',
  ];
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
