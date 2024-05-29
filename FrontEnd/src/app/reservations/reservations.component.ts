import { Component, inject, OnInit } from '@angular/core';
import { ReservationService } from './reservation.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CalendarEvent } from 'angular-calendar';
import { MatDialog } from '@angular/material/dialog';
import { GalleryComponent } from '../gallery/gallery.component';

@Component({
  selector: 'app-reservations',
  templateUrl: './reservations.component.html',
  styleUrl: './reservations.component.css',
})
export class ReservationsComponent implements OnInit {
  private reservationService = inject(ReservationService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private dialog = inject(MatDialog);

  protected sportCenter: any;
  protected currentField: any;
  protected sportCenterId: any;
  protected viewDate: Date = new Date();
  protected reservations: CalendarEvent[] = [];
  protected minHours: number = 1;
  protected maxHours: number = 23;

  ngOnInit(): void {
    this.sportCenterId = this.route.snapshot.paramMap.get('id');
    const sport = this.route.snapshot.paramMap.get('sport');
    this.reservationService
      .getSportCenter(this.sportCenterId, sport)
      .subscribe((sportCenter: any) => {
        console.log(sportCenter);
        this.sportCenter = sportCenter;
        this.currentField = sportCenter.fields[0];
        this.setTimes();
        this.maxHours = this.getMaxHours(this.currentField);
        this.minHours = this.getMinHours(this.currentField);
        this.reservationService
          .getReservations(sport, this.sportCenterId, new Date())
          .subscribe((reservations) => {
            console.log(reservations);
          });
      });
  }

  checkSportSelected($event: any) {
    this.router
      .navigate(['/reservations/' + $event.value + '/' + this.sportCenterId])
      .then(() => {
        window.location.reload();
      });
  }

  changeCurrentField(field: string) {
    this.currentField = field;
    this.maxHours = this.getMaxHours(field);
    this.minHours = this.getMinHours(field);
    this.setTimes();
  }

  eventClicked(event: CalendarEvent) {
    console.log(event);
  }

  private getMaxHours(currentField: any) {
    let endTimes = currentField.fieldAvailabilities.map(
      (availability: any) => availability.endTime,
    );
    let maxHours = new Date('2000-01-01T' + endTimes[0] + ':00');
    for (let i = 1; i < endTimes.length; i++) {
      let time = new Date('2000-01-01T' + endTimes[i] + ':00');
      if (time > maxHours) {
        maxHours = time;
      }
    }
    if (maxHours.getMinutes() > 0) {
      maxHours.setHours(maxHours.getHours() + 1);
    }
    return maxHours.getHours();
  }

  private getMinHours(currentField: any) {
    let startTimes = currentField.fieldAvailabilities.map(
      (availability: any) => availability.startTime,
    );
    let minHours = new Date('2000-01-01T' + startTimes[0] + ':00');
    for (let i = 1; i < startTimes.length; i++) {
      let time = new Date('2000-01-01T' + startTimes[i] + ':00');
      if (time < minHours) {
        minHours = time;
      }
    }
    return minHours.getHours();
  }

  checkTimeSelected($event: any) {
    console.log($event.value);
  }

  private setTimes() {
    this.currentField.timeSlots = [];

    for (
      let i = this.currentField.minResTime;
      i <= 150;
      i += this.currentField.timeSlot
    ) {
      if (i / 60 >= 1) {
        let hours = Math.floor(i / 60);
        let minutes = i % 60 ? ' ' + (i % 60) + ' min' : '';
        this.currentField.timeSlots.push(hours + ' h' + minutes);
      } else {
        this.currentField.timeSlots.push(i + ' min');
      }
    }
    this.currentField.timeSelected = this.currentField.timeSlots[0];
  }

  showGallery() {
    this.dialog.open(GalleryComponent, {
      width: '80%',
      height: '75%',
      data: [this.currentField.images, this.currentField.description],
    });
  }
}
