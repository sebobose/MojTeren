import {
  AfterViewInit,
  Component,
  inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { HomepageService } from './homepage.service';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import * as L from 'leaflet';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent implements OnInit, AfterViewInit {
  private homepageService = inject(HomepageService);
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);

  @ViewChild('searchArea') searchArea!: any;

  private map: any;
  private center: L.LatLngExpression = L.latLng(45.1, 15.2);
  markersGroup: any;
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
    // let mymap = L.map('map', {
    //   center: [45.1, 15.2],
    //   zoom: 7,
    // });
    //
    // L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    //   maxZoom: 18,
    //   minZoom: 3,
    //   attribution:
    //     '&copy; <a href="https://www.openstreetmap.org/copyright"></a>',
    // }).addTo(mymap);
    // mymap.addLayer(L.layerGroup());
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  getSport(sport: any) {
    this.activeSport = sport.name;
  }

  doSearch() {
    let address = this.searchArea.nativeElement.value;

    fetch('https://nominatim.openstreetmap.org/search?format=json&q=' + address)
      .then((response) => response.json())
      .then((data) => {
        if (data.length > 0) {
          let latlng: [number, number] = [
            parseFloat(data[0].lat),
            parseFloat(data[0].lon),
          ];
          this.map.setView(latlng, 13);
          L.marker(latlng).addTo(this.map).bindPopup(address).openPopup();
        } else {
          alert('Adresa nije pronađena');
        }
      })
      .catch((error) => {
        console.error('Došlo je do greške:', error);
      });
  }

  applyFilters() {}

  convertMinutesToTime(minutes: number): string {
    let hours = Math.floor(minutes / 60);
    let mins = minutes % 60;
    return `${hours}:${mins < 10 ? '0' : ''}${mins}`;
  }

  private initMap(): void {
    this.map = L.map('map', {
      center: this.center,
      zoom: 7,
    });

    const tiles = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        maxZoom: 18,
        minZoom: 3,
        attribution:
          '&copy; <a href="http://www.openstreetmap.org/copyright"></a>',
      },
    );

    var customIcon = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.5.1/dist/images/marker-icon.png', // Specify the path to your custom icon image
      iconSize: [22, 32], // Set the size of the icon
      iconAnchor: [16, 32], // Set the anchor point of the icon (relative to its size)
      popupAnchor: [0, -32], // Set the anchor point for popups (relative to its size)
    });
    //
    // var baseMaps = {
    //   OpenStreetMap: tiles,
    // };

    tiles.addTo(this.map);

    // a layer group, used here like a container for markers
    this.markersGroup = L.layerGroup();
    this.map.addLayer(this.markersGroup);

    this.map.on('click', (e: any) => {
      fetch(
        'https://nominatim.openstreetmap.org/search?format=json&q=' +
          e.latlng.lat +
          ',' +
          e.latlng.lng,
      )
        .then((response) => response.json())
        .then((data) => {
          console.log(data);
          if (data.length > 0) {
            let latlng: [number, number] = [
              parseFloat(data[0].lat),
              parseFloat(data[0].lon),
            ];
            this.map.setView(latlng, 13);
            L.marker(latlng)
              .addTo(this.map)
              .bindPopup(data[0].display_name)
              .openPopup();
          } else {
            alert('Adresa nije pronađena');
          }
        })
        .catch((error) => {
          console.error('Došlo je do greške:', error);
        });
      return;
    });
  }
}
