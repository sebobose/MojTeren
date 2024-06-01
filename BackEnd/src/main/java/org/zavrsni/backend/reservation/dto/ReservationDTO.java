package org.zavrsni.backend.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.entityStatus.EntityStatus;
import org.zavrsni.backend.reservation.Reservation;

import java.util.List;

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
    private String statusMessage;

    public ReservationDTO(Reservation reservation, String role) {
        this.reservationId = reservation.getReservationId();
        this.date = reservation.getDate().toString();
        this.startTime = reservation.getStartTime().toString();
        this.endTime = reservation.getEndTime().toString();
        if (role.equals("FIELD_OWNER") || role.equals("ADMIN")) {
            this.username = reservation.getUser().getUsername();
            List<EntityStatus> reservationStatuses = reservation.getReservationStatuses();
            this.statusMessage = reservationStatuses.get(reservationStatuses.size() - 1).getStatusComment();
        }
        this.field = reservation.getField().getFieldName();
    }
}
