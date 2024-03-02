package org.zavrsni.backend.reservation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.reservationStatus.ReservationStatus;
import org.zavrsni.backend.user.User;

import java.sql.Time;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservation {

    @Id
    @GeneratedValue
    private Long reservationId = 0L;

    @NotNull
    private Time startTime;

    @NotNull
    private Time endTime;

    @OneToMany(mappedBy = "reservation")
    private List<ReservationStatus> reservationStatuses;

    @ManyToOne
    private User user;

    @ManyToOne
    private Field field;
}
