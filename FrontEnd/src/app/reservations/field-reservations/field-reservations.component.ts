import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { ReservationService } from '../reservation.service';
import { ActivatedRoute, Router } from '@angular/router';
import {
  CalendarDateFormatter,
  CalendarEvent,
  CalendarView,
} from 'angular-calendar';
import { MatDialog } from '@angular/material/dialog';
import { GalleryComponent } from '../../gallery/gallery.component';
import { CustomDateFormatter } from '../../custom/custom-date-formatter.provider';
import { MakeReservationDialogComponent } from './make-reservation-dialog.component';

@Component({
  selector: 'app-reservations',
  templateUrl: './field-reservations.component.html',
  styleUrl: './field-reservations.component.css',
  providers: [
    {
      provide: CalendarDateFormatter,
      useClass: CustomDateFormatter,
    },
  ],
})
export class FieldReservationsComponent implements OnInit {
  @ViewChild('weekView') weekView: any;
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
  protected lastDraggedIn: any;
  protected hourSegments: any;
  protected view: CalendarView = CalendarView.Week;
  protected locale: string = 'hr';
  protected showCalendar: boolean = false;

  ngOnInit(): void {
    this.sportCenterId = this.route.snapshot.paramMap.get('id');
    const sport = this.route.snapshot.paramMap.get('sport');
    this.reservationService
      .getSportCenter(this.sportCenterId, sport)
      .subscribe((sportCenter: any) => {
        this.sportCenter = sportCenter;
        this.currentField = sportCenter.fields[0];
        this.setTimes();
        this.maxHours = this.getMaxHours(this.currentField);
        this.minHours = this.getMinHours(this.currentField);
        this.hourSegments = 60 / this.currentField.timeSlot;
        this.removeNonWorkingHours();
        this.getReservations(new Date());
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
    this.reservations = [];
    this.showCalendar = false;
    this.removeNonWorkingHours();
    this.getReservations(this.viewDate);
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

  draggedIn($event: any) {
    if ($event.target.className.includes('cal-hour-segment')) {
      if (
        $event.target.childNodes[0].className &&
        $event.target.childNodes[0].className.includes('cal-time')
      ) {
        return;
      }
      let timeSelectedMinutes = this.timeStringToMinutes();
      this.checkLastDraggedIn();
      this.lastDraggedIn = $event;
      this.lastDraggedIn.size =
        timeSelectedMinutes / this.currentField.timeSlot;
      let current = $event.target;
      current.style.backgroundColor = '#ebcb53';
      current.style.borderBottom = 'none';
      for (
        let i = 1;
        i < timeSelectedMinutes / this.currentField.timeSlot;
        i++
      ) {
        if (current.parentNode.nextElementSibling) {
          current = current.parentNode.nextElementSibling.childNodes[1];
        } else {
          if (!current.parentNode.parentNode.nextElementSibling) {
            this.lastDraggedIn.size = i;
            break;
          } else {
            current =
              current.parentNode.parentNode.nextElementSibling.childNodes[0]
                .childNodes[1];
          }
        }

        current.style.backgroundColor = '#ebcb53';
        if (i != timeSelectedMinutes / this.currentField.timeSlot - 1) {
          current.style.borderBottom = 'none';
        }
      }
    } else {
      this.checkLastDraggedIn();
      this.lastDraggedIn = null;
    }
  }

  checkLastDraggedIn() {
    if (this.lastDraggedIn) {
      let current = this.lastDraggedIn.target;
      current.style.backgroundColor = '#323437';
      current.style.borderBottom = '1px dashed #e1e1e1';
      for (let i = 1; i < this.lastDraggedIn.size; i++) {
        if (current.parentNode.nextElementSibling) {
          current = current.parentNode.nextElementSibling.childNodes[1];
        } else {
          current =
            current.parentNode.parentNode.nextElementSibling.childNodes[0]
              .childNodes[1];
        }
        current.style.backgroundColor = '#323437';
        current.style.borderBottom = '1px dashed #e1e1e1';
      }
    }
  }

  private timeStringToMinutes() {
    let time = this.currentField.timeSelected;
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

  makeReservation($event: { date: Date; sourceEvent: MouseEvent }) {
    if (!localStorage.getItem('token')) {
      alert('Morate biti prijavljeni za rezerviranje termina.');
      return;
    }

    let timeSelectedMinutes = this.timeStringToMinutes();
    if (
      this.lastDraggedIn.size <
      timeSelectedMinutes / this.currentField.timeSlot
    ) {
      alert('Molimo odaberite ispravan termin rezervacije.');
      return;
    }

    let eventEnd = new Date($event.date);
    let dateNow = new Date();
    eventEnd.setMinutes($event.date.getMinutes() + timeSelectedMinutes);
    if ($event.date < dateNow || eventEnd < dateNow) {
      alert('Molimo odaberite ispravan termin rezervacije.');
      return;
    }

    let sameDayReservations: any = this.reservations.filter((reservation) => {
      return (
        reservation.start.getDate() == $event.date.getDate() &&
        reservation.start.getMonth() == $event.date.getMonth() &&
        reservation.start.getFullYear() == $event.date.getFullYear()
      );
    });

    for (let i = 0; i < sameDayReservations.length; i++) {
      if (
        $event.date < sameDayReservations[i].start &&
        eventEnd > sameDayReservations[i].start
      ) {
        alert('Molimo odaberite ispravan termin rezervacije.');
        return;
      }
    }
    let startMinutes = $event.date.getMinutes().toString();
    let endMinutes = eventEnd.getMinutes().toString();
    if (startMinutes.length < 2) startMinutes = '0' + startMinutes;
    if (endMinutes.length < 2) endMinutes = '0' + endMinutes;
    let date = $event.date.toISOString().split('T')[0].split('-');
    const dialogRef = this.dialog.open(MakeReservationDialogComponent, {
      width: '600px',
      height: '520px',
      data: {
        sportCenterName: this.sportCenter.sportCenterName,
        fieldName: this.currentField.fieldName,
        date: date.reverse().join('.'),
        startTime: $event.date.getHours() + ':' + startMinutes,
        endTime: eventEnd.getHours() + ':' + endMinutes,
      },
    });
    dialogRef.afterClosed().subscribe((reason: any) => {
      if (reason) {
        let data = {
          fieldId: this.currentField.fieldId,
          date: $event.date,
          startTime: $event.date.getHours() + ':' + startMinutes,
          endTime: eventEnd.getHours() + ':' + endMinutes,
          message: reason[1],
          email: reason[2],
        };
        this.reservationService.makeReservation(data).subscribe({
          next: () => {
            window.location.reload();
          },
        });
      }
    });
  }

  weekChanged() {
    this.reservations = [];
    this.showCalendar = false;
    this.removeNonWorkingHours();
    this.getReservations(this.viewDate);
  }

  private removeNonWorkingHours() {
    let monday = this.getMonday();
    let daysInWeek: any = {
      Sunday: 0,
      Monday: 1,
      Tuesday: 2,
      Wednesday: 3,
      Thursday: 4,
      Friday: 5,
      Saturday: 6,
    };
    let minHours = this.minHours.toString();
    let maxHours = this.maxHours.toString();
    if (minHours.length == 1) {
      minHours = '0' + minHours;
    }
    if (maxHours.length == 1) {
      maxHours = '0' + maxHours;
    }
    for (let i = 0; i < this.currentField.fieldAvailabilities.length; i++) {
      let diff = daysInWeek[this.currentField.fieldAvailabilities[i].dayOfWeek];
      delete daysInWeek[this.currentField.fieldAvailabilities[i].dayOfWeek];
      let date = new Date(monday.toISOString().split('T')[0]);
      date.setDate(date.getDate() + diff);
      let startBefore = new Date(
        date.toISOString().split('T')[0] + 'T' + minHours + ':00',
      );
      let endBefore = new Date(
        date.toISOString().split('T')[0] +
          'T' +
          this.currentField.fieldAvailabilities[i].startTime +
          ':00',
      );
      let startAfter = new Date(
        date.toISOString().split('T')[0] +
          'T' +
          this.currentField.fieldAvailabilities[i].endTime +
          ':00',
      );
      let endAfter = new Date(
        date.toISOString().split('T')[0] + 'T' + maxHours + ':00',
      );
      if (startBefore < endBefore) {
        this.reservations.push(
          this.createClosedReservation(startBefore, endBefore),
        );
      }
      if (startAfter < endAfter) {
        this.reservations.push(
          this.createClosedReservation(startAfter, endAfter),
        );
      }
    }
    let nonWorkingDays: number[] = Object.values(daysInWeek);
    for (let i = 0; i < nonWorkingDays.length; i++) {
      let diff: number = nonWorkingDays[i];
      let date = new Date(monday.toISOString().split('T')[0]);
      date.setDate(date.getDate() + diff);
      let start = new Date(
        date.toISOString().split('T')[0] + 'T' + minHours + ':00',
      );
      let end = new Date(
        date.toISOString().split('T')[0] + 'T' + maxHours + ':00',
      );
      this.reservations.push(this.createClosedReservation(start, end));
    }
  }

  private getMonday() {
    let d = new Date(this.viewDate);
    d.setHours(0);
    let day = d.getDay();
    let diff = d.getDate() - day + (day == 0 ? -6 : 1);
    return new Date(d.setDate(diff));
  }

  private createClosedReservation(start: Date, end: Date) {
    return {
      start: start,
      end: end,
      title: 'Zatvoreno',
      color: {
        primary: '#e3bc08',
        secondary: '#FDF1BA',
      },
      resizable: {
        beforeStart: false,
        afterEnd: false,
      },
      draggable: false,
    };
  }

  private getReservations(date: Date) {
    this.reservationService
      .getReservations(date, this.currentField.fieldId)
      .subscribe((reservations: any) => {
        console.log(reservations);
        for (let i = 0; i < reservations.length; i++) {
          let reservation = {
            start: new Date(
              reservations[i].date + 'T' + reservations[i].startTime,
            ),
            end: new Date(reservations[i].date + 'T' + reservations[i].endTime),
            title: 'Rezervirano',
            color: {
              primary: '#e3bc08',
              secondary: '#FDF1BA',
            },
            resizable: {
              beforeStart: false,
              afterEnd: false,
            },
            draggable: false,
          };
          this.reservations.push(reservation);
        }
        this.showCalendar = true;
      });
  }
}
