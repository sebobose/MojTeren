import { Component, inject, OnInit } from '@angular/core';
import { ReservationService } from './reservation.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-reservations',
  templateUrl: './reservations.component.html',
  styleUrl: './reservations.component.css',
})
export class ReservationsComponent implements OnInit {
  private reservationService = inject(ReservationService);
  private route = inject(ActivatedRoute);

  ngOnInit(): void {
    const sportCenterId = this.route.snapshot.paramMap.get('id');
    const sport = this.route.snapshot.paramMap.get('sport');
    this.reservationService
      .getSportCenter(sportCenterId, sport)
      .subscribe((sportCenter) => {
        console.log(sportCenter);
        this.reservationService
          .getReservations(sport, sportCenterId, new Date())
          .subscribe((reservations) => {
            console.log(reservations);
          });
      });
  }
}
