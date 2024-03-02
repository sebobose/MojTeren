package org.zavrsni.backend.reservationStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.reservation.Reservation;
import org.zavrsni.backend.status.Status;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReservationStatus {

    @Id
    @GeneratedValue
    private Long reservationStatusId = 0L;

    @NotNull
    private Timestamp statusChangeTS;

    @ManyToOne
    private Status status;

    @ManyToOne
    private Reservation reservation;
}
