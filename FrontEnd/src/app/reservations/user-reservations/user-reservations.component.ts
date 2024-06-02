import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { SportsData } from '../../admin/admin-sports/admin-sports.component';
import { ReservationService } from '../reservation.service';
import { MatDialog } from '@angular/material/dialog';
import { DelReservationDialogComponent } from './del-reservation-dialog.component';

@Component({
  selector: 'app-user-reservations',
  templateUrl: './user-reservations.component.html',
  styleUrls: ['./user-reservations.component.css'],
})
export class UserReservationsComponent implements OnInit {
  private reservationService = inject(ReservationService);
  private dialog = inject(MatDialog);

  displayedColumns: string[] = [
    'time',
    'sportCenterName',
    'address',
    'fieldName',
    'sportName',
    'status',
    'statusMessage',
    'cancel',
  ];
  statusMap: any = {
    ACTIVE: 'Aktivna',
    INACTIVE: 'Otkazana',
    FINISHED: 'Završena',
  };
  dataSource: MatTableDataSource<SportsData> = new MatTableDataSource();
  disabledButtons: Map<string, boolean> = new Map<string, boolean>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatTable) table!: MatTable<any>;

  ngOnInit(): void {
    this.reservationService.getUserReservations().subscribe({
      next: (reservations: any) => {
        console.log(reservations);
        for (let reservation of reservations) {
          let date = reservation.time.split(' ')[0];
          date = date.split('-').reverse().join('.');
          let startTime = reservation.time.split(' ')[1];
          startTime = startTime.split(':').slice(0, 2).join(':');
          let endTime = reservation.time.split(' ')[3];
          endTime = endTime.split(':').slice(0, 2).join(':');
          reservation.time = date + ' ' + startTime + ' - ' + endTime;
          reservation.status = this.statusMap[reservation.status];
          this.disabledButtons.set(
            reservation.reservationId,
            reservation.status != 'Aktivna',
          );
        }
        reservations.sort((a: any, b: any) => {
          let sortAscending = 1;
          if (a.status != 'Aktivna' && b.status == 'Aktivna') {
            return 1;
          }
          if (a.status != 'Aktivna' && b.status != 'Aktivna') {
            sortAscending = -1;
          }
          if (a.status == 'Aktivna' && b.status != 'Aktivna') {
            return -1;
          }
          if (this.getYear(a.time) != this.getYear(b.time)) {
            return this.getYear(a.time) > this.getYear(b.time)
              ? sortAscending
              : -1 * sortAscending;
          } else if (this.getMonth(a.time) != this.getMonth(b.time)) {
            return this.getMonth(a.time) > this.getMonth(b.time)
              ? sortAscending
              : -1 * sortAscending;
          } else if (this.getDay(a.time) != this.getDay(b.time)) {
            return this.getDay(a.time) > this.getDay(b.time)
              ? sortAscending
              : -1 * sortAscending;
          } else {
            return this.getStartTime(a.time) > this.getStartTime(b.time)
              ? sortAscending
              : -1 * sortAscending;
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

  cancelReservation(element: any) {
    const dialogRef = this.dialog.open(DelReservationDialogComponent, {
      width: '600px',
      height: '500px',
      data: {
        sportCenterName: element.sportCenterName,
        fieldName: element.fieldName,
        time: element.time,
        sportName: element.sportName,
      },
    });
    dialogRef.afterClosed().subscribe((reason: any) => {
      if (reason) {
        this.reservationService
          .cancelReservation(element.reservationId, reason)
          .subscribe({
            next: () => {
              window.location.reload();
            },
          });
      }
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
