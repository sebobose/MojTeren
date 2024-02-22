import { Component, inject, OnInit } from '@angular/core';
import { HomepageService } from './homepage.service';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent implements OnInit {
  private homepageService = inject(HomepageService);

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
  distanceChange: FormControl = new FormControl(10);

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
}
