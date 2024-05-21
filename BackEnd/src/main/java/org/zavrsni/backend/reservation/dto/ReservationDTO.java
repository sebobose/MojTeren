package org.zavrsni.backend.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.reservation.Reservation;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long reservationId;
    private String date;
    private String startTime;
    private String endTime;
    private String username;
    private String field;

    public ReservationDTO(Reservation reservation, String role) {
        this.reservationId = reservation.getReservationId();
        this.date = reservation.getDate().toString();
        this.startTime = reservation.getStartTime().toString();
        this.endTime = reservation.getEndTime().toString();
        if (role.equals("FIELD_OWNER")) {
            this.username = reservation.getUser().getUsername();
        }
        this.field = reservation.getField().getFieldName();
    }
}
