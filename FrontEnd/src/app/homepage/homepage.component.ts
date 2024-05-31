import {
  ChangeDetectorRef,
  Component,
  inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { HomepageService } from './homepage.service';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
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
  mapHeight: any = window.innerHeight * 0.65 + 'px';

  ngOnInit(): void {
    if (localStorage.getItem('role') === 'ADMIN') {
      this.router.navigate(['/admin/requests']).then(() => {
        window.location.reload();
      });
    } else if (localStorage.getItem('role') === 'FIELD_OWNER') {
      this.router.navigate(['/field-owner/sport-centers']).then(() => {
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

  doSearch() {}

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
    let form = {
      latitude: this.lat,
      longitude: this.lng,
      distance: this.filterForm.value.distanceChange,
      date: this.filterForm.value.date,
      timeLow: this.filterForm.value.timeChangeLow,
      timeHigh: this.filterForm.value.timeChangeHigh,
      sport: this.activeSport,
    };

    this.homepageService.getSportCenters(form).subscribe({
      next: (data: any) => {
        console.log(data);
        this.sportCenters = data;
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
        this.changeDetectorRef.detectChanges();
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
    this.router
      .navigate([
        '/reservations/' +
          this.activeSport +
          '/' +
          this.activeSportCenter.sportCenterId,
      ])
      .then(() => {
        window.location.reload();
      });
  }
}
