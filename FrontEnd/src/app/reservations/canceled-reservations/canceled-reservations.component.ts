import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { ReservationService } from '../reservation.service';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { SportsData } from '../../admin/admin-sports/admin-sports.component';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'app-canceled-reservations',
  templateUrl: './canceled-reservations.component.html',
  styleUrls: ['./canceled-reservations.component.css'],
})
export class CanceledReservationsComponent implements OnInit {
  private reservationService = inject(ReservationService);

  displayedColumns: string[] = [
    'time',
    'sportCenterName',
    'address',
    'fieldName',
    'sportName',
    'statusMessage',
  ];
  dataSource: MatTableDataSource<SportsData> = new MatTableDataSource();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatTable) table!: MatTable<any>;

  ngOnInit(): void {
    this.reservationService.getCanceledReservations().subscribe({
      next: (reservations: any) => {
        for (let reservation of reservations) {
          let date = reservation.time.split(' ')[0];
          date = date.split('-').reverse().join('.');
          let startTime = reservation.time.split(' ')[1];
          startTime = startTime.split(':').slice(0, 2).join(':');
          let endTime = reservation.time.split(' ')[3];
          endTime = endTime.split(':').slice(0, 2).join(':');
          reservation.time = date + ' ' + startTime + ' - ' + endTime;
        }
        reservations.sort((a: any, b: any) => {
          if (this.getYear(a.time) != this.getYear(b.time)) {
            return this.getYear(a.time) > this.getYear(b.time) ? -1 : 1;
          } else if (this.getMonth(a.time) != this.getMonth(b.time)) {
            return this.getMonth(a.time) > this.getMonth(b.time) ? -1 : 1;
          } else if (this.getDay(a.time) != this.getDay(b.time)) {
            return this.getDay(a.time) > this.getDay(b.time) ? -1 : 1;
          } else {
            return this.getStartTime(a.time) > this.getStartTime(b.time)
              ? -1
              : 1;
          }
        });
        this.dataSource = new MatTableDataSource(reservations);
        this.dataSource.paginator = this.paginator;
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
  }

  private getYear(time: any) {
    return time.split(' ')[0].split('.')[2];
  }

  private getMonth(time: any) {
    return time.split(' ')[0].split('.')[1];
  }

  private getDay(time: any) {
    return time.split(' ')[0].split('.')[0];
  }

  private getStartTime(time: any) {
    return time.split(' ')[1];
  }
}
