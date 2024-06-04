import {
  ChangeDetectorRef,
  Component,
  inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { HomepageService } from './homepage.service';
import { FormBuilder } from '@angular/forms';
import { NavigationExtras, Router } from '@angular/router';
import { GoogleMap } from '@angular/google-maps';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent implements OnInit {
  private homepageService = inject(HomepageService);
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);
  private changeDetectorRef = inject(ChangeDetectorRef);

  @ViewChild('searchArea') searchArea!: any;
  @ViewChild(GoogleMap) mapComponent!: GoogleMap;

  options: google.maps.MapOptions = {
    center: { lat: 45.8, lng: 15.97 },
    zoom: 12,
    streetViewControl: false,
    mapTypeControl: false,
    fullscreenControl: false,
  };
  activeSport: any = 'Nogomet';
  sports: any;
  markers: any = [];
  lat: any;
  lng: any;
  sportCenters: any;
  activeSportCenter: any = null;
  filterForm: any = this.formBuilder.nonNullable.group(
    {
      distanceChange: [10],
      date: [''],
      timeChangeLow: [300],
      timeChangeHigh: [1439],
      reservationTime: ['1 h'],
    },
    {
      updateOn: 'change',
    },
  );
  minDate: any = new Date();
  mapHeight: any = window.innerHeight * 0.6 + 'px';
  timeSlots: string[] = [
    '45 min',
    '1 h',
    '1 h 15 min',
    '1 h 30 min',
    '1 h 45 min',
    '2 h',
    '2 h 15 min',
    '2 h 30 min',
  ];

  ngOnInit(): void {
    if (localStorage.getItem('role') === 'ADMIN') {
      this.router.navigate(['/admin/requests']).then(() => {
        window.location.reload();
      });
    } else if (localStorage.getItem('role') === 'FIELD_OWNER') {
      this.router.navigate(['/sport-centers-list']).then(() => {
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

  onMapReady() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          this.lat = position.coords.latitude;
          this.lng = position.coords.longitude;
          const pos = {
            lat: position.coords.latitude,
            lng: position.coords.longitude,
          };
          this.mapComponent.googleMap?.setCenter(pos);
          this.getLocations();
        },
        () => {
          console.log('Unable to retrieve your location');
        },
      );
    } else {
      console.log('Geolocation is not supported by this browser.');
    }
  }

  getSport(sport: any) {
    this.activeSport = sport.name;
    this.applyFilters();
  }

  doSearch() {
    this.markers = [];
    let form = {
      latitude: this.lat,
      longitude: this.lng,
      sport: this.activeSport,
      search: this.searchArea.nativeElement.value,
    };
    this.homepageService.searchSportCenters(form).subscribe({
      next: (data: any) => {
        this.sportCenters = data;
        this.setMarkers(data);
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
  }

  applyFilters() {
    this.markers = [];
    this.getLocations();
  }

  convertMinutesToTime(minutes: number): string {
    let hours = Math.floor(minutes / 60);
    let mins = minutes % 60;
    return `${hours}:${mins < 10 ? '0' : ''}${mins}`;
  }

  private getLocations() {
    let reservationTime = this.convertTimeToMinutes(
      this.filterForm.value.reservationTime,
    );
    let date = new Date(this.filterForm.value.date);
    date.setHours(10);
    let form = {
      latitude: this.lat,
      longitude: this.lng,
      distance: this.filterForm.value.distanceChange,
      date: date,
      timeLow: this.filterForm.value.timeChangeLow,
      timeHigh: this.filterForm.value.timeChangeHigh,
      sport: this.activeSport,
      reservationTime: reservationTime,
    };

    this.homepageService.getSportCenters(form).subscribe({
      next: (data: any) => {
        console.log(data);
        this.sportCenters = data;
        this.setMarkers(data);
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
  }

  getSportCenter(sportCenter: any) {
    if (this.activeSportCenter == sportCenter) {
      return;
    }
    this.activeSportCenter = sportCenter;
    this.options.center = {
      lat: parseFloat(sportCenter.latitude),
      lng: parseFloat(sportCenter.longitude),
    };
    if (this.options.zoom == 12) {
      this.options.zoom = 16;
      this.mapComponent.googleMap?.setOptions(this.options);
      this.changeDetectorRef.detectChanges();
    } else {
      this.options.zoom = 12;
      this.mapComponent.googleMap?.setOptions(this.options);
      this.changeDetectorRef.detectChanges();
      setTimeout(() => {
        this.options.zoom = 16;
        this.mapComponent.googleMap?.setOptions(this.options);
        this.changeDetectorRef.detectChanges();
      }, 1000);
    }
  }

  goToSportCenterFields() {
    let navigationExtras: NavigationExtras = {
      queryParams: {},
    };
    if (this.activeSportCenter.filteredField) {
      console.log(this.activeSportCenter.filteredField.fieldId);
      navigationExtras.queryParams = {
        field: this.activeSportCenter.filteredField.fieldId,
        date: this.filterForm.value.date,
      };
    }
    this.router
      .navigate(
        [
          '/reservations/' +
            this.activeSport +
            '/' +
            this.activeSportCenter.sportCenterId,
        ],
        navigationExtras,
      )
      .then(() => {
        window.location.reload();
      });
  }

  private convertTimeToMinutes(reservationTime: any) {
    let time = reservationTime;
    let minutes = 0;
    let hours = 0;
    if (time.includes('h')) {
      hours = parseInt(time.split(' ')[0]) * 60;
    }
    if (time.includes('min')) {
      if (time.split(' ').length > 2) {
        minutes = parseInt(time.split(' ')[2]);
      } else {
        minutes = parseInt(time.split(' ')[0]);
      }
    }
    return hours + minutes;
  }

  private setMarkers(data: any) {
    for (let i = 0; i < data.length; i++) {
      this.markers.push({
        position: {
          lat: parseFloat(data[i].latitude),
          lng: parseFloat(data[i].longitude),
        },
        sportCenter: data[i],
      });
      this.sportCenters[i].distance = data[i].distance.toFixed(1);
      if (data[i].fields.length == 1) {
        this.sportCenters[i].fieldNum = '1 teren';
      } else {
        this.sportCenters[i].fieldNum = data[i].fields.length + ' terena';
      }
    }
    this.sportCenters.sort((a: any, b: any) => {
      return a.distance - b.distance;
    });
    this.changeDetectorRef.detectChanges();
  }
}
